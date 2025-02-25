package com.api.stuv.domain.socket.dto;

import com.api.stuv.domain.socket.entity.ChatMessage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDto {
    private String id;
    private String roomId;
    private Long senderId;
    private String message;
    private List<Long> readBy;
    private LocalDateTime createdAt;

    public static ChatMessageResponseDto fromEntity(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                .id(chatMessage.getId())
                .roomId(chatMessage.getRoomId())
                .senderId(chatMessage.getSenderId())
                .message(chatMessage.getMessage())
                .readBy(chatMessage.getReadBy())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}
