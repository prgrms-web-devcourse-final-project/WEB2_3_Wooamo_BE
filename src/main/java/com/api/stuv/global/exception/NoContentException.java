package com.api.stuv.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class NoContentException extends RuntimeException {
  public NoContentException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    log.info("[NoContentException] message: {}", errorCode.getMessage());
  }
}
