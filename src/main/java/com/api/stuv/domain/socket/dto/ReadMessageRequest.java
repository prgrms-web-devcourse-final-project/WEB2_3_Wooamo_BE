package com.api.stuv.domain.socket.dto;

public record ReadMessageRequest(
        String roomId,
        Long userId
) {}
