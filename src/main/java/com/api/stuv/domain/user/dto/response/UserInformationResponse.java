package com.api.stuv.domain.user.dto.response;

public record UserInformationResponse(
        Long userId,
        String context,
        String link,
        String nickname,
        String profile,
        String status,
        Long friends,
        Long friendId
) {
}
