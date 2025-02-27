package com.api.stuv.domain.timer.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record TimerListResponse(
        Long timerId,
        Long categoryId,
        String name,
        LocalDate studyDate,
        LocalTime studyTime
) {
}
