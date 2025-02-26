package com.api.stuv.domain.socket.dto;

import com.api.stuv.domain.socket.entity.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageResponse(
        String id,
        String roomId,
        Long senderId,
        String message,
        List<Long> readBy,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage chatMessage){
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getRoomId(),
                chatMessage.getSenderId(),
                chatMessage.getMessage(),
                chatMessage.getReadBy(),
                chatMessage.getCreatedAt()
                );
    }
}
