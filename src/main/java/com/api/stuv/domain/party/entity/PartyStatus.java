package com.api.stuv.domain.party.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PartyStatus {
    UPCOMING("예정"),
    ACTIVE("진행"),
    ENDED("마감");

    private final String text;
}
