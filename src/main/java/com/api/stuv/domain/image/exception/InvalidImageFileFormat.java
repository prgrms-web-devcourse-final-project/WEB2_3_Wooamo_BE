package com.api.stuv.domain.image.exception;

import com.api.stuv.global.exception.BusinessException;
import com.api.stuv.global.exception.ErrorCode;

public class InvalidImageFileFormat extends BusinessException {
    public InvalidImageFileFormat() {
        super(ErrorCode.INVALID_REQUEST_BODY);
    }
}
