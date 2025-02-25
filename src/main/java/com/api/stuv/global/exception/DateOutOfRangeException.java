package com.api.stuv.global.exception;

import lombok.Getter;

@Getter
public class DateOutOfRangeException extends BusinessException {
    public DateOutOfRangeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
