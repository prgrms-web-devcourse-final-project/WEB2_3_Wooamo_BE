package com.api.stuv.global.exception;

import lombok.Getter;

@Getter
public class ValidationException extends BusinessException {
    public ValidationException() {
        super(ErrorCode.INVALID_ARGUMENT_METHOD);
    }
}
