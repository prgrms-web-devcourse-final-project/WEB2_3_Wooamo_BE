package com.api.stuv.domain.admin.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EventPartyRequest(
        String name,
        String context,
        Long recruitCap,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal bettingPointCap
) {
}
