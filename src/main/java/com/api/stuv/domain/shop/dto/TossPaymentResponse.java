package com.api.stuv.domain.shop.dto;

import java.math.BigDecimal;

public record TossPaymentResponse(
        String orderId,
        BigDecimal amount,
        BigDecimal point
) {}
