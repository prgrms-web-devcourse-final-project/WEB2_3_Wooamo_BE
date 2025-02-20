package com.api.stuv.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatus {
    SUCCESS("성공"),
    ERROR("실패");

    private final String text;
}
