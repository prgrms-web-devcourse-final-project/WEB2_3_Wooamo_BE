package com.api.stuv.domain.timer.service;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.timer.dto.UserRankDTO;
import com.api.stuv.domain.timer.dto.response.*;
import com.api.stuv.domain.timer.dto.request.AddTimerCategoryRequest;
import com.api.stuv.domain.timer.entity.Timer;
import com.api.stuv.domain.timer.repository.StudyTimeRepository;
import com.api.stuv.domain.timer.repository.TimerRepository;
import com.api.stuv.domain.user.dto.UserProfileInfoDTO;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.api.stuv.domain.timer.util.TimerUtil.formatSecondsToTime;

@Service
@RequiredArgsConstructor
public class TimerService {
    private final TokenUtil tokenUtil;
    private final StudyTimeRepository studyTimeRepository;
    private final TimerRepository timerRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String STUDY_KEY = "study:total";
    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;

    public List<TimerListResponse> getTimerList() {
        Long userId = tokenUtil.getUserId();
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        LocalDate date = LocalDate.now();

        return timerRepository.findByUserId(userId)
                .stream()
                .map(t -> new TimerListResponse(
                        t.getId(),
                        t.getName(),
                        date,
                        getStudyTime(userId, t.getId(), date)
                ))
                .toList();
    }

    private String getStudyTime(Long userId, Long categoryId, LocalDate date) {
        String key = String.format("%s:%s:%s", STUDY_KEY, userId, categoryId);
        Object obj = redisTemplate.opsForHash().get(key, date.toString());

        if (obj == null) {
            return formatSecondsToTime(studyTimeRepository.findStudyTimeById(userId, categoryId, date));
        }

        Long study = Optional.of(obj)
                .map(s -> Long.parseLong(s.toString()))
                .orElse(0L);

        return formatSecondsToTime(study);
    }

    public AddTimerCategoryResponse addTimerCategory(AddTimerCategoryRequest addTimerCategoryRequest) {
        Long userId = tokenUtil.getUserId();
        if(userId == null){
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        Timer timer = AddTimerCategoryRequest.from(addTimerCategoryRequest, userId);
        timerRepository.save(timer);
        return new AddTimerCategoryResponse(timer.getId());
    }

    public void recordStudyTime(Long categoryId, Long time) {
        Long userId = tokenUtil.getUserId();
        timerRepository.findByIdAndUserId(categoryId, userId).orElseThrow(() -> new NotFoundException(ErrorCode.TIMER_NOT_EXIST));

        String totalKey = String.format("%s:%s:%s", STUDY_KEY, userId, categoryId);
        String dateKey = LocalDate.now().toString();

        redisTemplate.opsForHash().put(totalKey, dateKey, time.toString());
    }

    public void deleteTimerCategory(Long categoryId) {
        Long userId = tokenUtil.getUserId();
        if(userId == null){
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        Timer timer = timerRepository.findByUserIdAndId(userId, categoryId);
        if(timer == null){
            throw new NotFoundException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        timerRepository.delete(timer);
    }

    public List<StudyDateTimeResponse> getMonthlyStudyRecord(int year, int month, Long userId) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = YearMonth.of(year, month).atEndOfMonth();
        if (!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);

        Map<LocalDate, Long> studyMap = studyTimeRepository.sumTotalStudyTimeByDate(userId, firstDay, lastDay);

        return firstDay.datesUntil(lastDay.plusDays(1))
                .map(date -> new StudyDateTimeResponse(
                        date,
                        formatSecondsToTime(studyMap.getOrDefault(date, 0L))
                ))
                .toList();
    }

    public StudyDateTimeResponse getWeeklyStudyRecord() {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Long userId = tokenUtil.getUserId();
        if (!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);

        return new StudyDateTimeResponse(
                formatSecondsToTime(
                        studyTimeRepository.sumTotalStudyTimeByWeekly(userId, startOfWeek, endOfWeek)
                )
        );
    }

    public StudyDateTimeResponse getDailyStudyRecord() {
        LocalDate today = LocalDate.now();
        Long userId = tokenUtil.getUserId();
        if (!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);

        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        Set<String> keys = redisTemplate.keys(STUDY_KEY + ":" + userId + ":*");

        if (keys == null || keys.isEmpty()) {
            return studyTimeRepository.sumTotalStudyTimeByDaily(userId, today);
        }

        long totalStudyTime = 0L;
        for (String key : keys) {
            String studyTime = hashOps.get(key, today.toString());
            if (studyTime != null) {
                System.out.println(Long.parseLong(studyTime));
                totalStudyTime += Long.parseLong(studyTime);
            }
        }

        return new StudyDateTimeResponse(today, formatSecondsToTime(totalStudyTime));
    }

    public RankResponse getUserRank() {
        Long userId = tokenUtil.getUserId();
        if (!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);

        Long rank = redisTemplate.opsForZSet().reverseRank("weekly_study_rank", userId.toString());

        if (rank == null) {
            return new RankResponse("-");
        }

        return new RankResponse(String.valueOf(rank + 1));
    }

    public List<RankInfoResponse> getTopRankUser() {
        Set<ZSetOperations.TypedTuple<String>> topUsers = redisTemplate.opsForZSet().reverseRangeWithScores("weekly_study_rank", 0, 2);

        if (topUsers == null || topUsers.isEmpty()) {
            return Collections.emptyList();
        }

        LinkedHashMap<Long, Long> studyTimeMap = new LinkedHashMap<>();
        List<Long> userIds = new ArrayList<>();

        for (ZSetOperations.TypedTuple<String> tuple : topUsers) {
            if (tuple.getValue() != null && tuple.getScore() != null) {
                Long userId = Long.parseLong(tuple.getValue());
                Long studyTime = tuple.getScore().longValue();
                studyTimeMap.put(userId, studyTime);
                userIds.add(userId);
            }
        }

        List<UserProfileInfoDTO> userInfoList = userRepository.findUserInfoByIds(userIds);

        Map<Long, UserProfileInfoDTO> userInfoMap = userInfoList.stream()
                .collect(Collectors.toMap(UserProfileInfoDTO::userId, Function.identity()));

        return userIds.stream()
                .map(userId -> {
                    UserProfileInfoDTO dto = userInfoMap.get(userId);
                    if (dto == null) return null;

                    return new RankInfoResponse(
                            userId,
                            s3ImageService.generateImageFile(
                                    EntityType.COSTUME, dto.entityId(), dto.filename()
                            ),
                            dto.nickname(),
                            formatSecondsToTime(studyTimeMap.get(userId))
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }


    /* Initialize And Scheduler */
    public void updateWeeklyRanking() {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String rankUpdateKey = "weekly_study_rank";
        List<UserRankDTO> rankings = studyTimeRepository.findWeeklyUserRank(startOfWeek, endOfWeek, null);

        redisTemplate.delete(rankUpdateKey);
        if (rankings.isEmpty()) {
            return;
        }

        rankings.forEach(rank -> redisTemplate.opsForZSet().add(rankUpdateKey, rank.userId().toString(), rank.studyTime()));
    }
}
