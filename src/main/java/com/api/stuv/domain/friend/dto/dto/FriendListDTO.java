package com.api.stuv.domain.friend.dto.dto;

public record FriendListDTO(
        Long friendId,
        Long userId,
        String nickname,
        String context,
        Long costumeId,
        String newFilename,
        String status
) {}
