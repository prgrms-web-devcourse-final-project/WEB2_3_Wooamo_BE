package com.api.stuv.domain.socket.dto;

import com.api.stuv.domain.socket.entity.ChatMessage;
import com.api.stuv.domain.socket.entity.ChatRoom;

import java.time.LocalDateTime;

public record ChatRoomResponse(
        String roomId,
        String roomType,
        String roomName,
        Long lastSenderId,
        String lastMessage,
        LocalDateTime createdAt,
        String profileImageUrl,
        String groupName,
        int unreadCount
) {
    public static ChatRoomResponse from(ChatRoom room, ChatMessage latestMessage, String profileImageUrl, String groupName, int unreadCount) {
        boolean isPrivate = "PRIVATE".equals(room.getRoomType());

        return new ChatRoomResponse(
                room.getRoomId(),
                room.getRoomType(),
                isPrivate ? null : groupName,
                isPrivate ? (latestMessage != null ? latestMessage.getSenderId() : null) : null,
                latestMessage != null ? latestMessage.getMessage() : "대화 내역 없음",
                latestMessage != null ? latestMessage.getCreatedAt() : LocalDateTime.MIN,
                isPrivate ? profileImageUrl : null,
                isPrivate ? null : groupName,
                unreadCount
        );
    }
}

