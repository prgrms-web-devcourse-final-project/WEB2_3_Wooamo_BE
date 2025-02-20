package com.api.stuv.domain.user.dto.request;

public record UserRequest(
        String email,
        String password,
        String nickname
) {
}
