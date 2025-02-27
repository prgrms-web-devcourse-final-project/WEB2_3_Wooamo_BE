package com.api.stuv.global.util.scheduler;

import com.api.stuv.domain.timer.entity.StudyTime;
import com.api.stuv.domain.timer.repository.StudyTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimerScheduler {

    private final StringRedisTemplate redisTemplate;
    private final StudyTimeRepository studyTimeRepository;

    @Scheduled(cron = "0 */10 * * * *") // 10분마다 실행
    public void backupStudyTimeToDB() {
        log.info("== 공부 시간 백업 진행 중 (Redis → MariaDB) ==");
        LocalDate date = LocalDate.now();

        ScanOptions options = ScanOptions.scanOptions().match("study:total:*").count(100).build();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            cursor.forEachRemaining(key -> processStudyTimeBackup(key, date));
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 실행
    public void saveDailyStudyTime() {
        log.info("== 공부 시간 기록 진행 중 (Redis → MariaDB) ==");
        LocalDate yesterday = LocalDate.now().minusDays(1); // 어제 날짜

        ScanOptions options = ScanOptions.scanOptions().match("study:total:*").count(100).build();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            cursor.forEachRemaining(key -> processDailyStudyTimeBackup(key, yesterday));
        }
    }

    private void processDailyStudyTimeBackup(String key, LocalDate date) {
        boolean isSaved = processStudyTimeBackup(key, date);
        if (isSaved) {
            redisTemplate.opsForHash().delete(key, date.toString());
        }
    }

    private boolean processStudyTimeBackup(String key, LocalDate date) {
        try {
            String[] parts = key.split(":");
            if (parts.length < 4) {
                log.warn("잘못된 Redis 키 형식: {}", key);
                return false;
            }

            Long userId = Long.parseLong(parts[2]);
            Long categoryId = Long.parseLong(parts[3]);

            Object totalStudy = redisTemplate.opsForHash().get(key, date.toString());
            if (totalStudy == null) return false;

            long total;
            try {
                total = Long.parseLong(totalStudy.toString()); // 안전한 변환
            } catch (NumberFormatException e) {
                log.warn("유효하지 않은 공부 시간 데이터 [{}] for key: {}", totalStudy, key);
                return false;
            }

            studyTimeRepository.save(new StudyTime(userId, categoryId, date, total));
            return true;

        } catch (Exception e) {
            log.error("공부 시간 백업 중 오류 발생 (키: {}): {}", key, e.getMessage());
            return false;
        }
    }
}
