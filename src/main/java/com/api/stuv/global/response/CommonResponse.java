package com.api.stuv.global.response;

import lombok.Getter;

import java.time.Instant;

@Getter
public class CommonResponse<T> {
    private final String status;
    private final String message;
    private final String timestamp;
    private final T data;

    public CommonResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now().toString();
        this.data = data;
    }
}
