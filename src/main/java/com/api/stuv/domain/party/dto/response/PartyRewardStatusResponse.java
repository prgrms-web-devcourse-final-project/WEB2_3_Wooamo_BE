package com.api.stuv.domain.party.dto.response;

import java.math.BigDecimal;

public record PartyRewardStatusResponse(
        Long partyId,
        String name,
        BigDecimal rewardPoint,
        String questStatus
) {
}
