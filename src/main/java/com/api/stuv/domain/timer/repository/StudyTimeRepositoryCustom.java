package com.api.stuv.domain.timer.repository;

import java.time.LocalDate;

public interface StudyTimeRepositoryCustom {
    Long findStudyTimeById(Long userId, Long categoryId, LocalDate date);
}
