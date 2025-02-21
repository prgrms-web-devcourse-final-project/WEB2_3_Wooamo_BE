package com.api.stuv.domain.image.exception;

import com.api.stuv.global.exception.BusinessException;
import com.api.stuv.global.exception.ErrorCode;

public class ImageFileNotFound extends BusinessException {
    public ImageFileNotFound() {
        super(ErrorCode.INVALID_REQUEST_BODY);
    }
}
