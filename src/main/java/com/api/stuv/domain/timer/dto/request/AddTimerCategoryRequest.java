package com.api.stuv.domain.timer.dto.request;

import com.api.stuv.domain.timer.entity.Timer;

public record AddTimerCategoryRequest(
        String timer
) {
    public static Timer from(AddTimerCategoryRequest addTimerCategoryRequest, Long userId) {
        return Timer.builder()
                .userId(userId)
                .name(addTimerCategoryRequest.timer)
                .build();
    }
}
