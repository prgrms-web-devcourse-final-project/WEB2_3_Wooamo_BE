package com.api.stuv.domain.board.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BoardType {
    FREE("자유"),
    QUESTION("질문");

    private final String text;

    @Override
    public String toString() {
        return text;
    }
}
