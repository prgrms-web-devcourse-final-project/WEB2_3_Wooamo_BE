package com.api.stuv.domain.party.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
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

    private LocalDate confirmDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConfirmStatus confirmStatus;

    public QuestConfirm(Long memberId, LocalDate confirmDate) {
        this.memberId = memberId;
        this.confirmDate = confirmDate;
        this.confirmStatus = ConfirmStatus.PENDING;
    }
}