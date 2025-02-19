package com.api.stuv.domain.friend.dto;

import com.api.stuv.domain.friend.entity.Friend;

public record FriendResponse() {
    public record RequestFriend(
            Long friendId,
            Long senderId,
            Long receiverId
    ) {
        public static RequestFriend from(Friend friend) {
            return new RequestFriend(friend.getFriendId(), friend.getUserId(), friend.getFriendId());
        }
    }
}
