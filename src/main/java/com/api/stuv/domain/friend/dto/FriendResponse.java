package com.api.stuv.domain.friend.dto;

import com.api.stuv.domain.friend.entity.Friend;

public record FriendResponse() {
    public record RequestFriend(
            Long userId,
            Long friendId
    ) {
        public static RequestFriend from(Friend friend) {
            return new RequestFriend(friend.getUserId(), friend.getFriendId());
        }
    }
}
