package com.api.stuv.domain.friend.dto;

public record FriendFollowRequestListResponse(
        Long friendId,
        Long senderId,
        String profile,
        String nickname,
        String context
) {}
