package com.api.stuv.domain.friend.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FriendFollowStatus {
    ME("내가 요쳥한 친구"),
    OTHER("나에게 요청한 친구"),
    NONE("친구 아님");

    private final String text;
}