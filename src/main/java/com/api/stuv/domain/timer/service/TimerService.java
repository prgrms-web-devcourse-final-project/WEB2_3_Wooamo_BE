package com.api.stuv.domain.timer.service;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.timer.dto.request.AddTimerCatetoryRequest;
import com.api.stuv.domain.timer.dto.response.AddTimerCatetoryResponse;
import com.api.stuv.domain.timer.dto.response.TimerListResponse;
import com.api.stuv.domain.timer.entity.Timer;
import com.api.stuv.domain.timer.repository.StudyTimeRepository;
import com.api.stuv.domain.timer.repository.TimerRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimerService {
    private final TokenUtil tokenUtil;
    private final StudyTimeRepository studyTimeRepository;
    private final TimerRepository timerRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String STUDY_KEY = "study:total";

    public List<TimerListResponse> getTimerList() {
        Long userId = tokenUtil.getUserId();
        if(userId == null){
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        return studyTimeRepository.findAllStudyTimeByUserId(userId);
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
            throw new NotFoundException(ErrorCode.NOT_FOUND_CATEGORY);
        }

        timerRepository.delete(timer);
    }
}
