package com.api.stuv.domain.user.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "study_times")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long categoryId;

    private LocalDate studyDate;

    private LocalTime studyTime;

    @Builder
    public StudyTime(Long userId, Long categoryId, LocalDate studyDate, LocalTime studyTime) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.studyDate = studyDate;
        this.studyTime = studyTime;
    }
}

