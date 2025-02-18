package com.api.stuv.domain.user.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FriendStatus {
    PENDING("요청 중"),
    ACCEPTED("친구");

    private final String text;
}