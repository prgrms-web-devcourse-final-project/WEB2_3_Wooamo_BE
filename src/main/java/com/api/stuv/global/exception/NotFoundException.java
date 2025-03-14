package com.api.stuv.global.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    // ErrorCode 반환
    private final ErrorCode errorCode;

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}