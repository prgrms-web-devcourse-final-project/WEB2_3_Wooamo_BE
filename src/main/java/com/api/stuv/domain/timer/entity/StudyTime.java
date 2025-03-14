package com.api.stuv.domain.timer.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "study_times")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(StudyTimeId.class)
public class StudyTime {
    @Id private Long userId;

    @Id private Long categoryId;

    @Id private LocalDate studyDate;

    private Long studyTime;

    public StudyTime(Long userId, Long categoryId, LocalDate studyDate, Long studyTime) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.studyDate = studyDate;
        this.studyTime = studyTime;
    }
}

