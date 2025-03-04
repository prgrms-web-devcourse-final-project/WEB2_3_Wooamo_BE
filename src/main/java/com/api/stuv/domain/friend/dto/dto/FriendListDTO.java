package com.api.stuv.domain.friend.dto.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FriendListDTO(
        Long friendId,
        Long userId,
        String nickname,
        String context,
        Long costumeId,
        String newFilename,
        String status
) {}
