package com.api.stuv.domain.party.dto.response;

public record MemberResponse(
        Long friendId,
        Long userId,
        String nickname,
        String profile,
        String context,
        String status
) {
}
