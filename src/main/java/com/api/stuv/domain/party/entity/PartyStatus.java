package com.api.stuv.domain.party.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PartyStatus {
    APPROVED("승인 완료"),
    PENDING("미승인");

    private final String text;
}
