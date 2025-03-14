package com.api.stuv.domain.timer.dto.response;

import java.time.LocalDate;

public record TimerListResponse(
        Long categoryId,
        String name,
        LocalDate studyDate,
        String studyTime
) {
}
