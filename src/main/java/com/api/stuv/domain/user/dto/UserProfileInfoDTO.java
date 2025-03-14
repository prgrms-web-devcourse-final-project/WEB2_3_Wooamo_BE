package com.api.stuv.domain.user.dto;

public record UserProfileInfoDTO(
    Long userId,
    String nickname,
    String filename,
    Long entityId
) {
}
