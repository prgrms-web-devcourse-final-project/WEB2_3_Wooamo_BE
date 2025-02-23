package com.api.stuv.domain.friend.dto;

public record FriendSearchResponse(
        Long userId,
        String nickname,
        String profile,
        String context
) {}
