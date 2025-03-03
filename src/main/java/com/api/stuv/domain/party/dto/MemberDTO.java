package com.api.stuv.domain.party.dto;

public record MemberDTO(
        Long friendId,
        Long userId,
        String nickname,
        String context,
        Long imageId,
        String image,
        String status
) {
}
