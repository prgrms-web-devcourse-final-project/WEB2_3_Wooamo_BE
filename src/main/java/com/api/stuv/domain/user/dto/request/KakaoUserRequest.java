package com.api.stuv.domain.user.dto.request;

public record KakaoUserRequest(
        String email,
        String password,
        String nickname,
        Long socialId
) {
}
