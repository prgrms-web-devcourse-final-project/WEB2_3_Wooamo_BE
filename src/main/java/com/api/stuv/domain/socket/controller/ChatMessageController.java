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

    // roomId 방에 접속한 사용자 ID 저장
    private final ConcurrentMap<String, List<Long>> roomSessions = new ConcurrentHashMap<>();

    // 사용자가 방에 들어올 때 등록
    @MessageMapping("/chat/join")
    public void addUserToRoom(@Payload ReadMessageDto readMessageDto) {
        String roomId = readMessageDto.getRoomId();
        Long userId = readMessageDto.getUserId();

        // 사용자가 다른 방에 참여 중이면 해당 방에서 삭제
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

        // 사용자가 입장하면 해당 방의 모든 메시지를 읽음 처리
        chatMessageService.markMessagesAsRead(roomId, userId);
    }

    // 사용자가 방에서 나갈 때 삭제
    @MessageMapping("/chat/leave")
    public void removeUserFromRoom(@Payload ReadMessageDto readMessageDto) {
        String roomId = readMessageDto.getRoomId();
        Long userId = readMessageDto.getUserId();

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

    @MessageMapping("/chat/send")
    public void sendMessage(@Payload ChatMessageRequestDto message) {
        if (message.getRoomId() != null) { // roomId를 기준으로 메시지를 전송
            logger.info("메시지 전송 [{} -> {}]: {}", message.getSenderId(), message.getRoomId(), message.getMessage());

            // 현재 방에 있는 모든 사용자 가져오기
            List<Long> usersInRoom = roomSessions.getOrDefault(message.getRoomId(), Collections.emptyList());
            message.setReadBy(new ArrayList<>(new HashSet<>(usersInRoom)));
            ChatMessage savedMessage = chatMessageService.saveMessage(message);

            // TODO: 저장 후 전체 메시지 목록을 조회하여 브로드캐스트 (읽음 상태도 포함)
//            List<ChatMessage> messages = chatMessageService.findByRoomIdOrderByCreatedAtAsc(message.getRoomId());

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
