package com.api.stuv.domain.friend.dto;

import com.api.stuv.domain.friend.entity.Friend;
import com.api.stuv.domain.friend.entity.FriendStatus;

public class FriendResponse {
    public record FriendRequestResponse (
            Long friendId,
            Long senderId,
            Long receiverId,
            FriendStatus status
    ) {
        public static FriendRequestResponse from(Friend friend) {
            return new FriendRequestResponse (friend.getId(), friend.getUserId(), friend.getFriendId(), friend.getStatus());
        }
    }
}
