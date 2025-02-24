package com.api.stuv.domain.image.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EntityType {
    BOARD("게시글","board"),
    EVENT("이벤트","event"),
    CONFIRM("인증","confirm"),
    COSTUME("코스튬","costume/costume");

    private final String text;
    private final String path;

}
