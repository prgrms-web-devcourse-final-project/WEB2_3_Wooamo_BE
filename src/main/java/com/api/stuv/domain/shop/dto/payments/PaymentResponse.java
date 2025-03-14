package com.api.stuv.domain.shop.dto.payments;

import java.math.BigDecimal;

public record PaymentResponse(
        String orderId,
        BigDecimal amount,
        BigDecimal point
) {}
