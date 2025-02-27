package com.api.stuv.domain.timer.dto.request;

import java.time.LocalTime;

public record StudyTimeRequest(
        LocalTime time
) {
    public Long convertTimeToSeconds() {
        return (long) this.time.toSecondOfDay();
    }
}
