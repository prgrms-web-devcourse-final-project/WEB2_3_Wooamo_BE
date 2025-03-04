package com.api.stuv.domain.friend.dto.dto;

public record FriendRecommendDTO(
        Long userId,
        String nickname,
        String context,
        Long costumeId,
        String newFilename
) {}
