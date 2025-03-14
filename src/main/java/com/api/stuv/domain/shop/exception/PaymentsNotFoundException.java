package com.api.stuv.domain.shop.exception;

import com.api.stuv.global.exception.BusinessException;
import com.api.stuv.global.exception.ErrorCode;

public class PaymentsNotFoundException extends BusinessException {
    public PaymentsNotFoundException() {
        super(ErrorCode.PAYMENTS_NOT_FOUND);
    }
}
