package com.api.stuv.domain.socket.dto;

import com.api.stuv.domain.socket.entity.ChatMessage;
import com.api.stuv.domain.socket.entity.ChatRoom;

import java.time.LocalDateTime;

public record ChatRoomResponse(
        String roomId,
        String roomType,
        String roomName,    // 그룹 채팅이면 방 이름 저장
        Long lastSenderId,  // 1:1 채팅이면 마지막 메시지 보낸 사람 ID 저장
        String lastMessage,
        LocalDateTime createdAt,
        String profileImageUrl  // ✅ 1:1 채팅일 경우 마지막 메시지 보낸 사람의 프로필 이미지 URL 추가
) {
    public static ChatRoomResponse from(ChatRoom room, ChatMessage latestMessage, String profileImageUrl) {
        boolean isPrivate = "PRIVATE".equals(room.getRoomType());

        return new ChatRoomResponse(
                room.getRoomId(),
                room.getRoomType(),
                isPrivate ? null : room.getRoomName(),  // ✅ 그룹이면 방 이름 설정, private이면 null
                isPrivate ? (latestMessage != null ? latestMessage.getSenderId() : null) : null,  // ✅ 1:1이면 마지막 보낸 사람 ID 저장
                latestMessage != null ? latestMessage.getMessage() : "대화 내역 없음",
                latestMessage != null ? latestMessage.getCreatedAt() : LocalDateTime.MIN,
                isPrivate ? profileImageUrl : null // ✅ private이면 마지막 보낸 사람의 프로필 URL, 그룹이면 null 반환
        );
    }
}
