package com.api.stuv.domain.shop.exception;

import com.api.stuv.global.exception.BusinessException;
import com.api.stuv.global.exception.ErrorCode;

public class PointNotEnoughException extends BusinessException {
    public PointNotEnoughException() {
        super(ErrorCode.POINT_NOT_ENOUGH);
    }
}
