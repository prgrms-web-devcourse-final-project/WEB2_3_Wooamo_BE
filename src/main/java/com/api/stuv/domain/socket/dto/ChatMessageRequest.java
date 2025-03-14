package com.api.stuv.domain.socket.dto;

import java.util.List;

public record ChatMessageRequest(
        String roomId,
        UserInfo userInfo,
        String message,
        List<Long> readBy
) {}