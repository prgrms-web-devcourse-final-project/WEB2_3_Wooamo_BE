package com.api.stuv.domain.socket.dto;

import com.api.stuv.domain.socket.entity.ChatMessage;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageResponse(
        String chatId,
        String roomId,
        UserInfo userInfo,
        String message,
        Integer readByCount,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage chatMessage, UserInfo userInfo, int readByCount) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getRoomId(),
                userInfo,
                chatMessage.getMessage(),
                readByCount,
                chatMessage.getCreatedAt()
        );
    }
}
