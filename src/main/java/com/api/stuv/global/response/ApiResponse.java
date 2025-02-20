package com.api.stuv.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(String status, String message, T data) {

    // 반환 O 성공
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseStatus.SUCCESS.getText(), null, data);
    }

    // 반환 X 성공
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ResponseStatus.SUCCESS.getText(), null,null);
    }

    // Error
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ResponseStatus.ERROR.getText(), message, null);
    }
}