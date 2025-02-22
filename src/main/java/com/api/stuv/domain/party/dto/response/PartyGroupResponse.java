package com.api.stuv.domain.party.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PartyGroupResponse(
        Long partyId,
        String name,
        Long recruitCap,
        Long recruitCnt,
        LocalDate startDate,
        LocalDate endDate
) {
}