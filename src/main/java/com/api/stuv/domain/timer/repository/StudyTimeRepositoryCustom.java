package com.api.stuv.domain.timer.repository;

import java.time.LocalDate;
import java.util.Map;

public interface StudyTimeRepositoryCustom {
    Long findStudyTimeById(Long userId, Long categoryId, LocalDate date);
    Map<LocalDate, Long> sumTotalStudyTimeByDate(Long userId, LocalDate startDate, LocalDate endDate);
    Long sumTotalStudyTimeByWeekly(Long userId, LocalDate startOfWeek, LocalDate endOfWeek);
    Long sumTotalStudyTimeByDaily(Long userId, LocalDate today);
}
