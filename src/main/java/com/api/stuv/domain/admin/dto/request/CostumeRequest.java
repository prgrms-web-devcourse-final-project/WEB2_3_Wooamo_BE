package com.api.stuv.domain.admin.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CostumeRequest(
        @NotBlank(message = "커스튬 이름을 입력해주세요")
        String costumeName,
        BigDecimal point
) {}
