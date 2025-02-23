package com.api.stuv.domain.admin.exception;

import com.api.stuv.global.exception.BusinessException;
import com.api.stuv.global.exception.ErrorCode;

public class CostumeNotFound extends BusinessException {
    public CostumeNotFound() {
        super(ErrorCode.COSTUME_NOT_FOUNT);
    }
}
