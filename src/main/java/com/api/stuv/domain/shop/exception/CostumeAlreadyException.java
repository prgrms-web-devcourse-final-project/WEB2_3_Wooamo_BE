package com.api.stuv.domain.shop.exception;

import com.api.stuv.global.exception.BusinessException;
import com.api.stuv.global.exception.ErrorCode;

public class CostumeAlreadyException extends BusinessException {
    public CostumeAlreadyException() {
        super(ErrorCode.COSTUME_ALREADY_PRESENT);
    }
}
