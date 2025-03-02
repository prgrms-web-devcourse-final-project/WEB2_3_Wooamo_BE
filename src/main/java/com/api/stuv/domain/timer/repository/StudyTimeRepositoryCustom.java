package com.api.stuv.domain.timer.repository;

import com.api.stuv.domain.timer.dto.UserRankDTO;
import com.api.stuv.domain.timer.dto.response.StudyDateTimeResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StudyTimeRepositoryCustom {
    Long findStudyTimeById(Long userId, Long categoryId, LocalDate date);
    Map<LocalDate, Long> sumTotalStudyTimeByDate(Long userId, LocalDate startDate, LocalDate endDate);
    Long sumTotalStudyTimeByWeekly(Long userId, LocalDate startOfWeek, LocalDate endOfWeek);
    StudyDateTimeResponse sumTotalStudyTimeByDaily(Long userId, LocalDate today);
    List<UserRankDTO> findWeeklyUserRank(LocalDate startOfWeek, LocalDate endOfWeek, Integer limit);
}
