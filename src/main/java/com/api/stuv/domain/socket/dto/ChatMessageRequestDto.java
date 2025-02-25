package com.api.stuv.domain.socket.dto;

import lombok.*;

import java.util.List;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDto {
    private String roomId;
    private Long senderId;
    private String message;
    private List<Long> readBy;
}