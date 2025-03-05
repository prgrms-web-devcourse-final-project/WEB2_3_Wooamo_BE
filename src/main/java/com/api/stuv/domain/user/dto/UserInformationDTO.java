package com.api.stuv.domain.user.dto;

public record UserInformationDTO(
        Long userId,
        String context,
        String link,
        String nickname,
        String newFilename,
        Long costumeId,
        String status,
        Long friends,
        Long friendId
) { }
