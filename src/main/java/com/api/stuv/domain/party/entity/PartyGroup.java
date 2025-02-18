package com.api.stuv.domain.party.entity;

import com.api.stuv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "party_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartyGroup extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long managerId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Column(precision = 10, nullable = false)
    private BigDecimal bettingPoint;

    private Long usersCount;

    @Builder
    public PartyGroup(Long managerId, String name, String context, BigDecimal bettingPoint, Long usersCount) {
        this.managerId = managerId;
        this.name = name;
        this.context = context;
        this.bettingPoint = bettingPoint;
        this.usersCount = usersCount;
    }
}
