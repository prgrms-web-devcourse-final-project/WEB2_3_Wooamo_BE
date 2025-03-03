package com.api.stuv.domain.party.dto.request;

import java.math.BigDecimal;

public record PartyJoinRequest(
        BigDecimal bettingPoint
) {
}
