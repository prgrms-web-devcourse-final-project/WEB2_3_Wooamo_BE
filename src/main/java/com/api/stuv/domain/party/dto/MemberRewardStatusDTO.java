package com.api.stuv.domain.party.dto;

import com.api.stuv.domain.party.entity.QuestStatus;

import java.math.BigDecimal;

public record MemberRewardStatusDTO(
        Long partyId,
        String partyName,
        BigDecimal bettingPoint,
        QuestStatus questStatus
) {
}
