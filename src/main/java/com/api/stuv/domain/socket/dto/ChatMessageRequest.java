package com.api.stuv.domain.socket.dto;

import java.util.List;

public record ChatMessageRequest(
        String roomId,
        Long senderId,
        String message,
        List<Long> readBy
) {}