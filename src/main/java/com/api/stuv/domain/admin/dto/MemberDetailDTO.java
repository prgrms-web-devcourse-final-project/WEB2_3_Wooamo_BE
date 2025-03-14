package com.api.stuv.domain.admin.dto;

import com.api.stuv.domain.party.entity.ConfirmStatus;

public record MemberDetailDTO(
        Long memberId,
        String nickname,
        ConfirmStatus status,
        String filename,
        Long costumeId
) {
}
