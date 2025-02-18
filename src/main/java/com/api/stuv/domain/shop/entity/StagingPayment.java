package com.api.stuv.domain.shop.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Getter
@ToString
@RedisHash("stagingPayment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StagingPayment {

    @Id
    private String orderId;

    private Long userId;

    private BigDecimal amount;

    private BigDecimal point;

    @TimeToLive
    private Long expiration; // TTL (초 단위)

    @Builder
    public StagingPayment(String orderId, Long userId, BigDecimal amount, BigDecimal point) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.point = point;
        this.expiration = TimeUnit.MINUTES.toSeconds(10L);
    }
}
