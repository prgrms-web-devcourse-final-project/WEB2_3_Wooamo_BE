package com.api.stuv.domain.shop.dto;

import java.math.BigDecimal;

public record CostumeListResponse(
        Long costumeId,
        String image,
        String costumeName,
        BigDecimal point
) {}
