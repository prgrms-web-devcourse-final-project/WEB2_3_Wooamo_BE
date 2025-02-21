package com.api.stuv.global.exception;

public class NoContentException extends RuntimeException {
  // ErrorCode 반환
  private final ErrorCode errorCode;

  public NoContentException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
