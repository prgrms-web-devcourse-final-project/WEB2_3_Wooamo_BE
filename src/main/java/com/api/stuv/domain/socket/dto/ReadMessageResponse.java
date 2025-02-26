package com.api.stuv.domain.socket.dto;

public record ReadMessageResponse(
        String roomId,
        Long userId
) {
}
