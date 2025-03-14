package com.api.stuv.domain.shop.dto.costume;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CostumeResponse(
        Long costumeId,
        String image,
        String costumeName,
        BigDecimal point
) {}
