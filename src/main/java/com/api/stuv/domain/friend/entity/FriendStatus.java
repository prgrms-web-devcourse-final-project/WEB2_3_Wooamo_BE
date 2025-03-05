package com.api.stuv.domain.friend.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FriendStatus {
    NOT_FRIEND("친구 아님"),
    PENDING("요청 중"),
    ACCEPTED("친구");

    private final String text;
}