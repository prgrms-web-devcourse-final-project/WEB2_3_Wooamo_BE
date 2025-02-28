package com.api.stuv.domain.timer.service;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.timer.dto.request.AddTimerCatetoryRequest;
import com.api.stuv.domain.timer.dto.response.AddTimerCatetoryResponse;
import com.api.stuv.domain.timer.dto.response.TimerListResponse;
import com.api.stuv.domain.timer.entity.Timer;
import com.api.stuv.domain.timer.repository.StudyTimeRepository;
import com.api.stuv.domain.timer.repository.TimerRepository;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimerService {
    private final TokenUtil tokenUtil;
    private final StudyTimeRepository studyTimeRepository;
    private final TimerRepository timerRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String STUDY_KEY = "study:total";
    private final UserRepository userRepository;

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

    private String formatSecondsToTime(Long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    public AddTimerCatetoryResponse addTimerCategory(AddTimerCatetoryRequest  addTimerCatetoryRequest) {
        Long userId = tokenUtil.getUserId();
        if(userId == null){
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        Timer timer = AddTimerCatetoryRequest.from(addTimerCatetoryRequest, userId);
        timerRepository.save(timer);
        return new AddTimerCatetoryResponse(timer.getId());
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
}
