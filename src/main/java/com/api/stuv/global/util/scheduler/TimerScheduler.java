package com.api.stuv.global.util.scheduler;

import com.api.stuv.domain.timer.dto.UserRankDTO;
import com.api.stuv.domain.timer.entity.StudyTime;
import com.api.stuv.domain.timer.repository.StudyTimeRepository;
import com.api.stuv.domain.timer.service.TimerService;
import com.api.stuv.domain.user.entity.HistoryType;
import com.api.stuv.domain.user.entity.PointHistory;
import com.api.stuv.domain.user.entity.RewardType;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.PointHistoryRepository;
import com.api.stuv.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimerScheduler {

    private final StringRedisTemplate redisTemplate;
    private final StudyTimeRepository studyTimeRepository;
    private final TimerService timerService;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 */10 * * * *") // 10분 마다 실행
    public void backupStudyTimeToDB() {
        log.info("== 공부 시간 백업 진행 중 (Redis → MariaDB) ==");
        LocalDate date = LocalDate.now();

        ScanOptions options = ScanOptions.scanOptions().match("study:total:*").count(100).build();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            cursor.forEachRemaining(key -> processStudyTimeBackup(key, date));
        }
        log.info("== 공부 시간 백업 완료 (Redis → MariaDB) ==");
    }

    @Scheduled(cron = "0 1 0 * * *") // 매일 자정 실행 - 어제 기록 삭제
    public void saveDailyStudyTime() {
        log.info("== 전날 공부 시간 초기화 진행 중 (Redis: delete) ==");
        var ref = new Object() {
            long count = 0;
        };
        LocalDate yesterday = LocalDate.now().minusDays(1); // 어제 날짜

        ScanOptions options = ScanOptions.scanOptions().match("study:total:*").count(500).build();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            cursor.forEachRemaining(key -> {
                Long deletedCount = redisTemplate.opsForHash().delete(key, yesterday.toString()); // 어제 날짜 데이터 삭제
                if (deletedCount > 0) {
                    ref.count++;
                }
            });
        }
        log.info("== 전날 공부 시간 초기화 완료 (Redis: {}) ==", ref.count);
    }

    @Scheduled(cron = "0 3 * * * *") // 1시간 마다 실행 - 랭킹 최신화
    public void updateWeeklyStudyTimeRankScheduler() {
        log.info("== 공부 시간 주간 랭킹 최신화 진행 중 (MariaDB -> Redis) ==");
        timerService.updateWeeklyRanking();
        log.info("== 공부 시간 주간 랭킹 최신화 완료 (MariaDB -> Redis) ==");
    }

    @Transactional
    @Scheduled(cron = "0 5 0 * * MON")
    public void processWeeklyRankingReward() {
        LocalDate startOfLastWeek = LocalDate.now().minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfLastWeek = startOfLastWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        log.info("== 공부 시간 주간 랭킹 보상 지급 중 ({} ~ {}) ==", startOfLastWeek, endOfLastWeek);
        List<UserRankDTO> rankings = studyTimeRepository.findWeeklyUserRank(startOfLastWeek, endOfLastWeek, 3);

        if (rankings.isEmpty()) {
            log.info("== 랭킹 데이터 없음 : 보상 지급 중단 ==");
            return;
        }

        RewardType[] rewards = {RewardType.RANK_1, RewardType.RANK_2, RewardType.RANK_3};

        for (int i = 0; i < rankings.size(); i++) {
            UserRankDTO dto = rankings.get(i);
            RewardType rewardType = rewards[i];

            User user = userRepository.findById(dto.userId()).orElse(null);
            if (user == null) {
                log.warn("== 존재하지 않는 회원 ==");
                continue;
            }

            boolean alreadyRewarded = pointHistoryRepository.existsHistoryByUserIdBetweenWeekends(dto.userId());
            if (alreadyRewarded) {
                log.warn("== {}번 회원 이미 보상 지급됨. ({}p) ==", dto.userId(), rewardType.getValue());
                continue;
            }

            user.earnReward(rewardType);

            PointHistory rewardHistory = new PointHistory(
                    dto.userId(),
                    HistoryType.RANKING,
                    rewardType.getValue(),
                    HistoryType.RANKING.getText()
            );

            pointHistoryRepository.save(rewardHistory);
            log.info("사용자 {} 보상 지급: {} ({}원)", dto.userId(), rewardType.name(), rewardType.getValue());
        }

        log.info("== 공부 시간 주간 랭킹 보상 완료 ==");
    }

    private void processStudyTimeBackup(String key, LocalDate date) {
        try {
            String[] parts = key.split(":");
            if (parts.length < 4) {
                log.warn("== 잘못된 Redis 키 형식: {} ==", key);
                return;
            }

            Long userId = Long.parseLong(parts[2]);
            Long categoryId = Long.parseLong(parts[3]);

            Object totalStudy = redisTemplate.opsForHash().get(key, date.toString());
            if (totalStudy == null) return;

            long total;
            try {
                total = Long.parseLong(totalStudy.toString()); // 안전한 변환
            } catch (NumberFormatException e) {
                log.warn("== 유효하지 않은 공부 시간 데이터 [{}] for key: {} ==", totalStudy, key);
                return;
            }

            studyTimeRepository.save(new StudyTime(userId, categoryId, date, total));

        } catch (Exception e) {
            log.error("== 공부 시간 백업 중 오류 발생 (키: {}): {} ==", key, e.getMessage());
        }
    }
}
