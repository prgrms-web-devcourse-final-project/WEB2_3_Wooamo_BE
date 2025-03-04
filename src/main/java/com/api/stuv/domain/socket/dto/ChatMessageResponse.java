package com.api.stuv.domain.socket.dto;

import com.api.stuv.domain.socket.entity.ChatMessage;

import java.util.List;

public record ChatMessageResponse(
        String chatId,
        String roomId,
        UserInfo userInfo,
        String message,
        Integer readByCount
) {
    public static ChatMessageResponse from(ChatMessage chatMessage, UserInfo userInfo) {
        List<Long> readBy = chatMessage.getReadBy();
        int count = (readBy != null) ? readBy.size() : 0;
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getRoomId(),
                userInfo,
                chatMessage.getMessage(),
                count
                );
    }
}
