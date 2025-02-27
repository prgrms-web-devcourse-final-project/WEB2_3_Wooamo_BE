package com.api.stuv.domain.timer.service;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.timer.dto.response.TimerListResponse;
import com.api.stuv.domain.timer.repository.StudyTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimerService {
    private final TokenUtil tokenUtil;
    private final StudyTimeRepository studyTimeRepository;

    public List<TimerListResponse> getTimerList() {
        Long userId = tokenUtil.getUserId();

        List<TimerListResponse> timerList = studyTimeRepository.findAllStudyTimeByUserId(userId);
        return timerList;
    }
}
