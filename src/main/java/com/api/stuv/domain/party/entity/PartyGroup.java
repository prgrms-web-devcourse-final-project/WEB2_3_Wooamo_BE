package com.api.stuv.domain.party.entity;

import com.api.stuv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "party_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartyGroup extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean isEvent;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Column(precision = 10, nullable = false)
    private BigDecimal bettingPoint;

    @Column(nullable = false)
    private Long usersCount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyStatus status;

    public PartyGroup(String name, String context, BigDecimal bettingPoint, Long usersCount, LocalDate startDate, LocalDate endDate) {
        this.isEvent = false;
        this.name = name;
        this.context = context;
        this.bettingPoint = bettingPoint;
        this.usersCount = usersCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = PartyStatus.PENDING;
    }
}
