package com.api.stuv.domain.shop.exception;

import com.api.stuv.global.exception.BusinessException;
import com.api.stuv.global.exception.ErrorCode;

public class CostumeNotPurchaseException extends BusinessException {
  public CostumeNotPurchaseException() {
    super(ErrorCode.COSTUME_NOT_PURCHASE);
  }
}
