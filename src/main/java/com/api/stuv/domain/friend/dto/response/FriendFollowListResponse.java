package com.api.stuv.domain.friend.dto.response;

public record FriendFollowListResponse(
        Long friendId,
        Long senderId,
        String profile,
        String nickname,
        String context
) {}
