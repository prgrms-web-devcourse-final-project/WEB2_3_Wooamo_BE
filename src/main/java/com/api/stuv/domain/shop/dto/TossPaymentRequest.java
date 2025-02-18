package com.api.stuv.domain.shop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class TossPaymentRequest {
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
}
