package com.api.stuv.domain.party.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "quest_confirm")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestConfirm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Boolean isConfirm;

    private LocalDate confirmDate;

    @Builder
    public QuestConfirm(Long memberId, Boolean isConfirm, LocalDate confirmDate) {
        this.memberId = memberId;
        this.isConfirm = isConfirm;
        this.confirmDate = confirmDate;
    }
}