package com.api.stuv.domain.socket.controller;

import com.api.stuv.domain.socket.dto.ChatMessageRequestDto;
import com.api.stuv.domain.socket.dto.ChatMessageResponseDto;
import com.api.stuv.domain.socket.dto.ReadMessageDto;
import com.api.stuv.domain.socket.entity.ChatMessage;
import com.api.stuv.domain.socket.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageController.class);

    @MessageMapping("/chat/send")
    public void sendMessage(@Payload ChatMessageRequestDto message) {
        if (message.getRoomId() != null) { // roomId를 기준으로 메시지를 전송
            logger.info("메시지 전송 [{} -> {}]: {}", message.getSenderId(), message.getRoomId(), message.getMessage());
            ChatMessage savedMessage = chatMessageService.saveMessage(message);

            // 저장된 메시지를 채팅방 구독자에게 전송
            messagingTemplate.convertAndSend("/topic/messages/" + message.getRoomId(), savedMessage);
        }
    }

    @MessageMapping("/chat/read")
    public void updateReadBy(@Payload ReadMessageDto readMessageDto,
                             @Header(value = "page", required = false) Integer page,
                             @Header(value = "size", required = false) Integer size) {

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 10;

        // 읽음 처리 업데이트
        chatMessageService.markMessagesAsRead(readMessageDto.getRoomId(), readMessageDto.getUserId());

        // 업데이트 후, 해당 채팅방의 전체 메시지 목록을 다시 가져옴
        List<ChatMessageResponseDto> updatedMessages = chatMessageService.getMessagesByRoomIdPagination(readMessageDto.getRoomId(),pageValue,sizeValue);

        // 모든 구독자에게 최신 메시지 목록을 전송하여 읽음 상태 갱신
        messagingTemplate.convertAndSend("/topic/messages/" + readMessageDto.getRoomId(), updatedMessages);
    }

}
