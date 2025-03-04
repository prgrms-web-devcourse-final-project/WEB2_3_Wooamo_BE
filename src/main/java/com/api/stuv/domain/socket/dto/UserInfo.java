package com.api.stuv.domain.socket.dto;

public record UserInfo(
        Long userId,
        String nickname,
        String profile
) {}

