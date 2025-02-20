package com.api.stuv.domain.friend.dto;

import com.api.stuv.domain.friend.entity.Friend;
import com.api.stuv.domain.friend.entity.FriendStatus;

public record FriendFollowResponse (
        Long friendId,
        Long senderId,
        Long receiverId,
        FriendStatus status
) {
    public static FriendFollowResponse from(Friend friend) {
        return new FriendFollowResponse (friend.getId(), friend.getUserId(), friend.getFriendId(), friend.getStatus());
    }
}

