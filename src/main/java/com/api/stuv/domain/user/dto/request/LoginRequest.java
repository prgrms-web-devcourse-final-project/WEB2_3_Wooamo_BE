package com.api.stuv.domain.user.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
