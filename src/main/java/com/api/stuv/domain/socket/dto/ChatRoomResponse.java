package com.api.stuv.domain.socket.dto;

import com.api.stuv.domain.socket.entity.ChatMessage;
import com.api.stuv.domain.socket.entity.ChatRoom;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ChatRoomResponse(
        String roomId,
        String roomName,
        String roomType,
        UserInfo lastUserInfo,
        UserInfo userInfo,
        GroupInfo groupInfo,
        String lastMessage,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        int unreadCount
) {
    public static ChatRoomResponse from(ChatRoom room, UserInfo lastUserInfo, UserInfo userInfo, GroupInfo groupInfo,ChatMessage latestMessage, int unreadCount) {
        return new ChatRoomResponse(
                room.getRoomId(),
                room.getRoomName(),
                room.getRoomType(),
                lastUserInfo,
                userInfo,
                groupInfo,
                latestMessage != null ? latestMessage.getMessage() : "대화 내역 없음",
                latestMessage != null ? latestMessage.getCreatedAt() : LocalDateTime.MAX,
                unreadCount
        );
    }
}

