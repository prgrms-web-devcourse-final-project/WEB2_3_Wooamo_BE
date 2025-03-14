package com.api.stuv.domain.user.entity;

import com.api.stuv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "point_history")
public class PointHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryType transactionType;

    @Column(precision = 10, nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String reason;

    @Builder
    public PointHistory(Long userId, HistoryType transactionType, BigDecimal amount, String reason) {
        this.userId = userId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.reason = reason;
    }
}