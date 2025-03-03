package com.api.stuv.global.exception;

public class SseErrorException extends RuntimeException {
    // ErrorCode 반환
    private final ErrorCode errorCode;

    public SseErrorException(String message) {
        super(message);
        this.errorCode = ErrorCode.SSE_ERROR;
    }

    public SseErrorException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
