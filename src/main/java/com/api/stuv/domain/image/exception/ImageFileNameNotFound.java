package com.api.stuv.domain.image.exception;

import com.api.stuv.global.exception.BusinessException;
import com.api.stuv.global.exception.ErrorCode;

public class ImageFileNameNotFound extends BusinessException {
    public ImageFileNameNotFound() {
        super(ErrorCode.IMAGE_NAME_NOT_FOUND);
    }
}
