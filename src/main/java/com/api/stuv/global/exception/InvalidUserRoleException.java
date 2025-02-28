package com.api.stuv.global.exception;

import lombok.Getter;

@Getter
public class InvalidUserRoleException extends BusinessException {
    public InvalidUserRoleException(ErrorCode errorCode) {
        super(errorCode);
    }
}
