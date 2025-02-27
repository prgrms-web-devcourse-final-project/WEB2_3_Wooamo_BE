package com.api.stuv.domain.timer.repository;

import com.api.stuv.domain.timer.dto.response.TimerListResponse;

import java.util.List;

public interface StudyTimeRepositoryCustom {
    List<TimerListResponse>  findAllStudyTimeByUserId(Long userId);
}
