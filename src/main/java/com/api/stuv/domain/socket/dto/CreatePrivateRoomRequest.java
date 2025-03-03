package com.api.stuv.domain.socket.dto;

public record CreatePrivateRoomRequest(
        Long userId1,
        Long userId2
) {
}
