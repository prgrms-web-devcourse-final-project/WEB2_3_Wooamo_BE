package com.api.stuv.domain.party.dto.response;

import java.time.LocalDate;

public record AdminPartyGroupResponse(
        Long partyId,
        String name,
        Long recruitCap,
        Long recruitCnt,
        LocalDate startDate,
        LocalDate endDate,
        String isApproved
) {
}