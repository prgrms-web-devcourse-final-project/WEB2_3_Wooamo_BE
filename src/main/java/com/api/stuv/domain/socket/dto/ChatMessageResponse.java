package com.api.stuv.domain.socket.dto;

import com.api.stuv.domain.socket.entity.ChatMessage;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageResponse(
        String id,
        String roomId,
        Long senderId,
        String message,
        Integer readByCount
) {
    public static ChatMessageResponse from(ChatMessage chatMessage){
        List<Long> readBy = chatMessage.getReadBy();
        int count = (readBy != null) ? readBy.size() : 0;
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getRoomId(),
                chatMessage.getSenderId(),
                chatMessage.getMessage(),
                count
                );
    }
}
