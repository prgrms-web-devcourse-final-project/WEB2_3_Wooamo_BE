package com.api.stuv.domain.admin.dto.response;

import java.math.BigDecimal;

public record EventPartyResponse(
        Long partyId,
        String image,
        String name,
        BigDecimal bettingPointCap
) {
}
