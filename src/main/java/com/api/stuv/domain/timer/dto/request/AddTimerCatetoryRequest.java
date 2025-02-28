package com.api.stuv.domain.timer.dto.request;

import com.api.stuv.domain.timer.entity.Timer;

public record AddTimerCatetoryRequest(
        String timer
) {
    public static Timer from(AddTimerCatetoryRequest addTimerCatetoryRequest, Long userId) {
        return Timer.builder()
                .userId(userId)
                .name(addTimerCatetoryRequest.timer)
                .build();
    }
}
