package com.api.stuv.domain.socket.dto;

import com.api.stuv.domain.socket.entity.ChatMessage;

import java.util.List;

public record ReadByResponse(
        String chatId,
        String roomId,
        Integer readByCount
) {
    public static ReadByResponse from(ChatMessage chatMessage) {
        List<Long> readBy = chatMessage.getReadBy();
        int count = (readBy != null) ? readBy.size() : 0;
        return new ReadByResponse(
                chatMessage.getId(),
                chatMessage.getRoomId(),
                count
        );
    }
}
