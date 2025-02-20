package com.api.stuv.global.exception;

import com.api.stuv.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // DB 관련 예외
    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<ApiResponse<Void>> handleDataAccessException(DataAccessException e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("[ERROR] handleDataAccessException - {}", e.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getMessage()));
    }

    // Not Found Exception
    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException e) {
        log.error("[ERROR] handleNotFoundException - {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.error(e.getErrorCode().getMessage()));
    }

    // Bad Request Exception
    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBadRequestException(BadRequestException e) {
        log.error("[ERROR] handleBadRequestException - {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.error(e.getErrorCode().getMessage()));
    }

    // Access Denied Exception
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("[ERROR] handleAccessDeniedException - {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.error(e.getErrorCode().getMessage()));
    }

    // Duplicate Exception
    @ExceptionHandler(DuplicateException.class)
    protected ResponseEntity<ApiResponse<Void>> handleDuplicateException(DuplicateException e) {
        log.error("[ERROR] handleDuplicateException - {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.error(e.getErrorCode().getMessage()));
    }

    // 그 외 예외
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("[ERROR] handleBusinessException - {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.error(e.getErrorCode().getMessage()));
    }

}
