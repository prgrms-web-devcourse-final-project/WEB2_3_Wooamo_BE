package com.api.stuv.domain.friend.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FriendStatus {
    PENDING("요청 중"),
    ACCEPTED("친구");

    private final String text;
}