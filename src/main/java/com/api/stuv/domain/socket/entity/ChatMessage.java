package com.api.stuv.domain.socket.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "chat_message") // MongoDB 컬렉션 이름 지정
public class ChatMessage {
    @Id
    private String id; // ObjectId
    private Long groupId; // 채팅방 ID
    private Long receiverId; // 받는 사람 ID
    private Long senderId; // 보낸 사람 ID
    private String message; // 메시지 내용
    private Long readCount; // 읽음 횟수

    @CreatedDate
    private LocalDateTime createdAt; // 생성 시간

    @Builder
    public ChatMessage(Long groupId, Long receiverId, Long senderId, String message, Long readCount) {
        this.groupId = groupId;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.message = message;
        this.readCount = readCount;
    }
}