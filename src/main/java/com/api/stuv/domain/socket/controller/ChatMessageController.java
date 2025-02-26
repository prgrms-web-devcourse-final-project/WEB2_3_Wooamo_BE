package com.api.stuv.domain.socket.controller;


import com.api.stuv.domain.socket.dto.ChatMessageResponse;
import com.api.stuv.domain.socket.dto.ChatMessageRequest;
import com.api.stuv.domain.socket.dto.ReadMessageResponse;
import com.api.stuv.domain.socket.dto.ReadMessageRequest;

import com.api.stuv.domain.socket.entity.ChatMessage;
import com.api.stuv.domain.socket.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Controller
@RequiredArgsConstructor
@Tag(name = "ChatMessage", description = "메세지 관련 WebSocket API")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageController.class);
    private final ConcurrentMap<String, List<Long>> roomSessions = new ConcurrentHashMap<>();

    @Operation(summary = "채팅방 입장", description = "사용자가 채팅방에 입장할 때 등록합니다.")
    @MessageMapping("/chat/join")
    public void addUserToRoom(@Payload ReadMessageRequest  readMessageRequest) {
        String roomId = readMessageRequest.roomId();
        Long userId = readMessageRequest.userId();

        roomSessions.forEach((rooms, users) -> {
            if (!rooms.equals(roomId) && users.contains(userId)) {
                users.remove(userId);
                logger.info("{} 사용자가 기존 채팅방 {}에서 제거되었습니다.", userId, rooms);
                if (users.isEmpty()) {
                    roomSessions.remove(rooms);
                }
            }
        });

        List<Long> usersInNewRoom = roomSessions.computeIfAbsent(roomId, key -> Collections.synchronizedList(new ArrayList<>()));
        if (!usersInNewRoom.contains(userId)) {
            usersInNewRoom.add(userId);
            logger.info("{} 사용자가 채팅방 {}에 참여", userId, roomId);
        } else {
            logger.info("{} 사용자가 이미 채팅방 {}에 참여 중", userId, roomId);
        }
        logger.info("현재 채팅방 사용자: {}", roomSessions);

        chatMessageService.markMessagesAsRead(roomId, userId);
    }

    @Operation(summary = "채팅방 퇴장", description = "사용자가 채팅방에서 나갈 때 삭제합니다.")
    @MessageMapping("/chat/leave")
    public void removeUserFromRoom(@Payload ReadMessageRequest  readMessageRequest) {
        String roomId = readMessageRequest.roomId();
        Long userId = readMessageRequest.userId();

        List<Long> users = roomSessions.get(roomId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }

        logger.info("{} 사용자가 채팅방 {}에서 나감", userId, roomId);
        logger.info("현재 채팅방 사용자: {}", roomSessions);
    }

    @Operation(summary = "메시지 전송", description = "채팅방에 메시지를 전송합니다.")
    @MessageMapping("/chat/send")
    public void sendMessage(@Payload ChatMessageRequest message) {
        if (message.roomId() != null) { // roomId를 기준으로 메시지를 전송
            logger.info("메시지 전송 [{} -> {}]: {}", message.senderId(), message.roomId(), message.message());

            // 현재 방에 있는 모든 사용자 가져오기
            List<Long> usersInRoom = roomSessions.getOrDefault(message.roomId(), Collections.emptyList());
            List<Long> readByList = new ArrayList<>(new HashSet<>(usersInRoom));

            ChatMessage savedMessage = chatMessageService.saveMessage(
                    new ChatMessageRequest(
                            message.roomId(),
                            message.senderId(),
                            message.message(),
                            readByList
                    )
            );

            // TODO: 저장 후 전체 메시지 목록을 조회하여 브로드캐스트 (읽음 상태도 포함)
//          List<ChatMessage> messages = chatMessageService.findByRoomIdOrderByCreatedAtAsc(message.roomId());

            // 저장된 메시지를 채팅방 구독자에게 전송
            messagingTemplate.convertAndSend("/topic/messages/" + message.roomId(), savedMessage);
        }
    }

    @Operation(summary = "읽음 상태 업데이트", description = "채팅방의 메시지를 읽음 처리로 변경합니다.")
    @MessageMapping("/chat/read")
    public void updateReadBy(@Payload ReadMessageRequest readMessageRequest,
                             @Header(value = "page", required = false) Integer page,
                             @Header(value = "size", required = false) Integer size) {

        int pageValue = (page != null) ? page : 0;
        int sizeValue = (size != null) ? size : 10;

        chatMessageService.markMessagesAsRead(readMessageRequest.roomId(), readMessageRequest.userId());

        List<ChatMessageResponse> updatedMessages = chatMessageService.getMessagesByRoomIdPagination(
                readMessageRequest.roomId(),
        PageRequest.of(pageValue, sizeValue));

        messagingTemplate.convertAndSend("/topic/messages/" + readMessageRequest.roomId(), updatedMessages);
    }

}
