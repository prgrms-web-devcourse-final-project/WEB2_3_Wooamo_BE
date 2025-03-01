package com.api.stuv.domain.timer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StudyDateTimeResponse(
        LocalDate studyDate,
        String studyTime
) {
    public StudyDateTimeResponse(String studyTime) {
        this(null, studyTime);
    }
}
