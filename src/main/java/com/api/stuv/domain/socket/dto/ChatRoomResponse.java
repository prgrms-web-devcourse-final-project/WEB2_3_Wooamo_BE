package com.api.stuv.domain.socket.dto;

import com.api.stuv.domain.socket.entity.ChatMessage;
import com.api.stuv.domain.socket.entity.ChatRoom;

import java.time.LocalDateTime;

public record ChatRoomResponse(
        String roomId,
        String roomType,
        UserInfo lastUserInfo,
        String lastMessage,
        LocalDateTime createdAt,
        int unreadCount
) {
    public static ChatRoomResponse from(ChatRoom room, UserInfo lastUserInfo, ChatMessage latestMessage, int unreadCount) {
        return new ChatRoomResponse(
                room.getRoomId(),
                room.getRoomType(),
                lastUserInfo,
                latestMessage != null ? latestMessage.getMessage() : "대화 내역 없음",
                latestMessage != null ? latestMessage.getCreatedAt() : LocalDateTime.MIN,
                unreadCount
        );
    }
}

