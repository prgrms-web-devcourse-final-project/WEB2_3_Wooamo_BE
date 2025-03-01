package com.api.stuv.domain.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentConfirmRequest(
        @NotBlank(message = "고유 주문 번호를 입력해주세요")
        String orderId,

        @NotBlank(message = "결제 키를 입력해주세요")
        String paymentKey,

        @NotNull(message = "가격을 입력해주세요")
        BigDecimal amount,

        @NotNull(message = "포인트를 입력해주세요")
        BigDecimal point
) {
}
