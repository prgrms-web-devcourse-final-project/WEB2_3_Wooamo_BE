package com.api.stuv.domain.party.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "group_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestStatus questStatus;

    @Column(precision = 10)
    private BigDecimal bettingPoint;

    public GroupMember(Long groupId, Long userId, QuestStatus questStatus, BigDecimal bettingPoint) {
        this.groupId = groupId;
        this.userId = userId;
        this.questStatus = questStatus;
        this.bettingPoint = bettingPoint;
    }

    public static GroupMember join(Long groupId, Long userId, BigDecimal bettingPoint) {
        return new GroupMember(
                groupId,
                userId,
                QuestStatus.PROGRESS,
                bettingPoint
        );
    }
}
