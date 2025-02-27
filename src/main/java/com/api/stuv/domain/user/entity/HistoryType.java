package com.api.stuv.domain.user.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HistoryType {
    CONSUME("사용"),
    PARTY("파티 보상"),
    PERSONAL("개인 퀘스트 보상"),
    CHARGE("충전");

    private final String text;

    public String getText() {
        return text;
    }
}
