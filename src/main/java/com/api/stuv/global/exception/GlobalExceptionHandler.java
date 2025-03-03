package com.api.stuv.global.exception;

import com.api.stuv.global.response.ApiResponse;
import com.querydsl.core.types.ExpressionException;
import io.lettuce.core.RedisConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // DB 관련 예외
    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<ApiResponse<Void>> handleDataAccessException(DataAccessException e) {
        log.error("[ERROR] handleDataAccessException - {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.DATABASE_ERROR.getStatus())
                .body(ApiResponse.error(ErrorCode.DATABASE_ERROR.getMessage()));
    }

    // 타입 변환 관련 예외
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErrorCode errorCode = ErrorCode.ARGUMENT_TYPE_MISMATCH;
        String paramName = e.getMethodParameter().getParameterName();
        Class<?> requiredTypeClass = e.getMethodParameter().getParameterType();
        String requiredType = requiredTypeClass.getSimpleName();
        String errorMessage = String.format("%s (파라미터명: %s, 요구 타입: %s)",
                errorCode.getMessage(), paramName, requiredType);
        log.error("[ERROR] handleMethodArgumentTypeMismatchException - {}", e.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorMessage));
    }

    // 날짜 변환 관련 예외
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiResponse<Void>> handleDateTimeParseException(DateTimeParseException e) {
        ErrorCode errorCode = ErrorCode.DATE_FORMAT_MISMATCH;
        log.error("[ERROR] handleDateTimeParseException - {}", e.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getMessage()));
    }

    @ExceptionHandler(ExpressionException.class)
    protected ResponseEntity<ApiResponse<Void>> handleExpressionException(ExpressionException e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("[ERROR] handleExpressionException - {}", e.getMessage());
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

    @ExceptionHandler(RedisConnectionException.class)
    protected ResponseEntity<ApiResponse<Void>> handleRedisConnectionException(RedisConnectionException e) {
        log.error("[ERROR] handleRedisConnectionException - {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.REDIS_NOT_CONNECTED.getStatus())
                .body(ApiResponse.error(ErrorCode.REDIS_NOT_CONNECTED.getMessage()));
    }

    @ExceptionHandler(SseErrorException.class)
    protected ResponseEntity<ApiResponse<Void>> handleSseErrorException(SseErrorException e) {
        log.error("[ERROR] handleSseErrorException - {}", e.getMessage());
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error(e.getMessage()));
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
