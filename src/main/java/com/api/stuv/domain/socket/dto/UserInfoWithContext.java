package com.api.stuv.domain.socket.dto;

public record UserInfoWithContext(
        Long userId,
        String nickname,
        String profile,
        String context
) {}

