package com.api.stuv.global.exception;

import lombok.Getter;

@Getter
public class DateOutOfRangeException extends RuntimeException {
    private final ErrorCode errorCode;

    public DateOutOfRangeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
