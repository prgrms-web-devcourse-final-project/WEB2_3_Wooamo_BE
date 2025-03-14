package com.api.stuv.domain.party.entity;

import com.api.stuv.domain.admin.dto.request.EventPartyRequest;
import com.api.stuv.domain.party.dto.request.PartyCreateRequest;
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
    private Long recruitCap;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyStatus status;

    public PartyGroup(Boolean isEvent, String name, String context, BigDecimal bettingPoint, Long recruitCap, LocalDate startDate, LocalDate endDate, PartyStatus status) {
        this.isEvent = isEvent != null ? isEvent : false;
        this.name = name;
        this.context = context;
        this.bettingPoint = bettingPoint;
        this.recruitCap = recruitCap;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status != null ? status : PartyStatus.PENDING;
    }

    public static PartyGroup create(PartyCreateRequest request) {
        return new PartyGroup(
                false,
                request.name(),
                request.context(),
                request.bettingPointCap(),
                request.recruitCap(),
                request.startDate(),
                request.endDate(),
                PartyStatus.PENDING
        );
    }

    public static PartyGroup createEvent(EventPartyRequest request) {
        return new PartyGroup(
                true,
                request.name(),
                request.context(),
                request.bettingPointCap(),
                request.recruitCap(),
                request.startDate(),
                request.endDate(),
                PartyStatus.PENDING
        );
    }
}
