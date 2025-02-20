package com.api.stuv.domain.image.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ImageType {
    BOARD("게시글"),
    EVENT("이벤트"),
    COSTUME("아이템"),
    CONFIRM("인증");

    private final String text;
}
