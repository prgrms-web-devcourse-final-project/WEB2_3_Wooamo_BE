package com.api.stuv.domain.party.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PartyCreateRequest(
        String name,
        String context,
        Long recruitCap,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal bettingPointCap,
        BigDecimal userBettingPoint
) {
}
