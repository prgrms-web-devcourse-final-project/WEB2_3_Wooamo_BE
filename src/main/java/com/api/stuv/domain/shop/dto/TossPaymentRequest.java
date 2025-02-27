package com.api.stuv.domain.shop.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TossPaymentRequest(
        @NotNull(message = "가격을 입력해주세요")
        BigDecimal amount,

        @NotNull(message = "포인트를 입력해주세요")
        BigDecimal point
) { }
