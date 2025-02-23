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

    public static BoardType fromText(String text) {
        for (BoardType type : BoardType.values()) {
            if (type.toString().equals(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid description: " + text);
    }
}
