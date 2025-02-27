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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimerService {
    private final TokenUtil tokenUtil;
    private final StudyTimeRepository studyTimeRepository;
    private final TimerRepository timerRepository;

    public List<TimerListResponse> getTimerList() {
        Long userId = tokenUtil.getUserId();
        if(userId == null){
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        List<TimerListResponse> timerList = studyTimeRepository.findAllStudyTimeByUserId(userId);
        return timerList;
    }

    public AddTimerCatetoryResponse addTimerCatetory(AddTimerCatetoryRequest  addTimerCatetoryRequest) {
        Long userId = tokenUtil.getUserId();
        if(userId == null){
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        Timer timer = AddTimerCatetoryRequest.from(addTimerCatetoryRequest, userId);
        timerRepository.save(timer);
        AddTimerCatetoryResponse addTimerCatetoryResponse = new AddTimerCatetoryResponse(timer.getId());
        return addTimerCatetoryResponse;
    }
}
