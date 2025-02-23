package com.api.stuv.domain.friend.dto;

public record FriendResponse(
        Long userId,
        String nickname,
        String context,
        String profile
) {}
