package com.api.stuv.domain.friend.dto;

public record FriendRequestListResponse(
        Long id,
        Long senderId,
        String profile,
        String nickname,
        String context
) {}
