package com.api.stuv.domain.user.dto.response;

public record UserInformationResponse(
        Long id,
        String context,
        String link,
        String nickname
) {
}
