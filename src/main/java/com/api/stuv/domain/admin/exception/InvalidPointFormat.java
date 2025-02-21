package com.api.stuv.domain.admin.exception;

import com.api.stuv.global.exception.BusinessException;
import com.api.stuv.global.exception.ErrorCode;

public class InvalidPointFormat extends BusinessException {
    public InvalidPointFormat() {
        super(ErrorCode.INVALID_REQUEST_BODY);
    }
}
