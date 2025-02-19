package com.api.stuv.domain.friend.dto;

public record FriendResponse() {
    public record RequestFriend(
            Long friendId
    ) {
        public static RequestFriend from(Long friendId) {
            return new RequestFriend(friendId);
        }
    }
}
