package com.api.stuv.domain.shop.dto.costume;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CostumeDTO(
        Long costumeId,
        String imageName,
        String costumeName,
        BigDecimal point
) {}
