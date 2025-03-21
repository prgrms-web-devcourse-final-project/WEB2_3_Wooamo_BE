package com.api.stuv.domain.shop.dto.payments;

import java.math.BigDecimal;

public record TossPaymentResponse(
        boolean success,
        String paymentKey,
        BigDecimal approvedAmount
) {
    public boolean isSuccess() {
        return success;
    }
}