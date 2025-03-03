package com.api.stuv.domain.party.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PartyDetailResponse(
        Long partyId,
        String name,
        Long recruitCap,
        Long recruitCnt,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal bettingPointCap,
        boolean isJoined
) {
}
