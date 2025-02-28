package com.api.stuv.domain.timer.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class StudyTimeId implements Serializable {
    private Long userId;
    private Long categoryId;
    private LocalDate studyDate;
}
