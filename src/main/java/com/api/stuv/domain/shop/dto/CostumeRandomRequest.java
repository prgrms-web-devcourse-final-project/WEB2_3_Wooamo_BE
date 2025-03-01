package com.api.stuv.domain.shop.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CostumeRandomRequest(
        @NotNull(message = "포인트를 입력해주세요")
        BigDecimal point
) {}
