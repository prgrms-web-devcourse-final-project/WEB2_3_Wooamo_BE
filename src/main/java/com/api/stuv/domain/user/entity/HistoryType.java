package com.api.stuv.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HistoryType {
    CONSUME("사용"),
    PARTY("파티 보상"),
    PERSONAL("개인 퀘스트 보상"),
    RANKING("주간 랭킹 보상"),
    CHARGE("충전");

    private final String text;
}
