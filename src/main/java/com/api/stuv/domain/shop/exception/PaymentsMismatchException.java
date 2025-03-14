package com.api.stuv.domain.shop.exception;

import com.api.stuv.global.exception.BusinessException;
import com.api.stuv.global.exception.ErrorCode;

public class PaymentsMismatchException extends BusinessException {
    public PaymentsMismatchException() {
        super(ErrorCode.PAYMENTS_MISMATCH);
    }
}
