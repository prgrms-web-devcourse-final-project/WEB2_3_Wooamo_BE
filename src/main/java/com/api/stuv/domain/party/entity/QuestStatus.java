package com.api.stuv.domain.party.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum QuestStatus {
    COMPLETED("보상완료"),
    SUCCESS("보상받기"),
    FAILED("획득실패"),
    PROGRESS("진행중");

    private final String text;

}