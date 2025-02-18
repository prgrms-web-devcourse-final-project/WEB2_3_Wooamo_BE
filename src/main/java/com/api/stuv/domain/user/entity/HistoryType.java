package com.api.stuv.domain.user.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HistoryType {
    CONSUME("사용"),
    REWARD("보상"),
    CHARGE("충전");

    private final String text;
}
