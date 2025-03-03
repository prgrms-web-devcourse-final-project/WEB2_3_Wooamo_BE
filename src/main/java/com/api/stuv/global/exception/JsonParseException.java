package com.api.stuv.global.exception;

import lombok.Getter;

@Getter
public class JsonParseException extends RuntimeException {
    // ErrorCode 반환
    private final ErrorCode errorCode;

    public JsonParseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
