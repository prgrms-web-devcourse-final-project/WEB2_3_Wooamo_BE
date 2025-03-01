package com.api.stuv.domain.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CostumePurchaseRequest(
        @NotNull(message = "코스튬 번호를 입력해주세요")
        Long costumeId,
        @NotNull(message = "포인트를 입력해주세요")
        BigDecimal point
) {}
