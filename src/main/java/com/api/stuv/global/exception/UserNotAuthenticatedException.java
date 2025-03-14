package com.api.stuv.global.exception;

import lombok.Getter;

@Getter
public class UserNotAuthenticatedException extends BusinessException {
    public UserNotAuthenticatedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
