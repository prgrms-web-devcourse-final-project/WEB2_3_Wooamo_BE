package com.api.stuv.domain.shop.entity;

import com.api.stuv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String orderId;

    @Column(length = 20, nullable = false, unique = true)
    private String paymentKey;

    @Column(nullable = false)
    private Long userId;

    @Column(precision = 10, nullable = false)
    private BigDecimal amount;

    @Column(precision = 10, nullable = false)
    private BigDecimal point;

    @Builder
    public Payment(String orderId, String paymentKey, Long userId, BigDecimal amount, BigDecimal point) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.userId = userId;
        this.amount = amount;
        this.point = point;
    }
}
