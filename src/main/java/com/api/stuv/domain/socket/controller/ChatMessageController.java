package com.api.stuv.domain.socket.controller;


import com.api.stuv.domain.socket.dto.*;

import com.api.stuv.domain.socket.service.ChatMessageService;
import com.api.stuv.domain.socket.service.ChatRoomDetailService;
import com.api.stuv.domain.socket.service.ChatRoomMemberService;
import com.api.stuv.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
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
    private final ChatRoomDetailService chatRoomDetailService;
    private final ChatRoomMemberService chatRoomMemberService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageController.class);
    private final ConcurrentMap<String, List<Long>> roomSessions = new ConcurrentHashMap<>();
    private final Set<Long> listPageSubscribers = ConcurrentHashMap.newKeySet();

    @Operation(summary = "채팅방 입장", description = "사용자가 채팅방에 입장할 때 등록합니다.")
    @MessageMapping("/chat/join")
    public void addUserToRoom(@Payload ReadMessageRequest readMessageRequest) {
        String roomId = readMessageRequest.roomId();
        Long userId = readMessageRequest.userId();

        chatRoomMemberService.userJoinRoom(userId, roomId);
        ChatRoomTypeInfoResponse chatRoomTypeInfoResponse  = chatRoomDetailService.getChatRoomInfoByRoomName(userId, roomId);

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
        messagingTemplate.convertAndSend("/topic/users/" + roomId, ApiResponse.success(chatRoomTypeInfoResponse));
    }

    @Operation(summary = "채팅방 퇴장", description = "사용자가 채팅방에서 나갈 때 삭제합니다.")
    @MessageMapping("/chat/leave")
    public void removeUserFromRoom(@Payload ReadMessageRequest readMessageRequest) {
        String roomId = readMessageRequest.roomId();
        Long userId = readMessageRequest.userId();

        List<Long> users = roomSessions.get(roomId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }

        chatRoomMemberService.userLeaveRoom(userId, roomId);

        logger.info("{} 사용자가 채팅방 {}에서 나감", userId, roomId);
        logger.info("현재 채팅방 사용자: {}", roomSessions);
    }

    @Operation(summary = "채팅 목록 페이지 입장", description = "사용자가 채팅 목록 페이지에 들어올 때 등록합니다.")
    @MessageMapping("/chat/list/join")
    public void addUserToListPage(@Payload ReadMessageRequest readMessageRequest) {
        Long userId = readMessageRequest.userId();
        if (listPageSubscribers.add(userId)) {
            logger.info("{} 사용자가 채팅방 목록 페이지에 입장했습니다.", userId);
        } else {
            logger.info("{} 사용자가 이미 채팅방 목록 페이지에 등록되어 있습니다.", userId);
        }
        logger.info("현재 목록 페이지 구독자: {}", listPageSubscribers);

        List<ChatRoomResponse> roomList = chatRoomDetailService.getSortedRoomListBySenderId(
                readMessageRequest.userId()
        );
        messagingTemplate.convertAndSend("/topic/rooms/" + userId, ApiResponse.success(roomList));
    }

    @Operation(summary = "채팅 목록 페이지 퇴장", description = "사용자가 채팅 목록 페이지를 나갈 때 제거합니다.")
    @MessageMapping("/chat/list/leave")
    public void removeUserFromListPage(@Payload ReadMessageRequest readMessageRequest) {
        Long userId = readMessageRequest.userId();
        listPageSubscribers.remove(userId);
        logger.info("{} 사용자가 채팅방 목록 페이지에서 나갔습니다.", userId);
        logger.info("현재 목록 페이지 구독자: {}", listPageSubscribers);
    }

    @Operation(summary = "메시지 전송", description = "채팅방에 메시지를 전송합니다.")
    @MessageMapping("/chat/send")
    public void sendMessage(@Payload ChatMessageRequest message) {
        if (message.roomId() != null) { // roomId를 기준으로 메시지를 전송
            logger.info("메시지 전송 [{} -> {}]: {}", message.userInfo().userId(), message.roomId(), message.message());

            // 현재 방에 있는 모든 사용자 가져오기
            List<Long> usersInRoom = roomSessions.getOrDefault(message.roomId(), Collections.emptyList());
            List<Long> readByList = new ArrayList<>(new HashSet<>(usersInRoom));

            ChatMessageResponse savedMessage = chatMessageService.saveMessage(
                    new ChatMessageRequest(
                            message.roomId(),
                            message.userInfo(),
                            message.message(),
                            readByList
                    )
            );

            // 저장된 메시지를 채팅방 구독자에게 전송
            messagingTemplate.convertAndSend("/topic/messages/" + message.roomId(), ApiResponse.success(savedMessage));
            // 채팅 목록 페이지에 있는 모든 사용자에게 업데이트 전송
            for (Long subscriberId : listPageSubscribers) {
                List<ChatRoomResponse> roomList = chatRoomDetailService.getSortedRoomListBySenderId(
                        subscriberId
                );
                messagingTemplate.convertAndSend("/topic/rooms/" + subscriberId, ApiResponse.success(roomList));
            }
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

        List<ReadByResponse> updatedReadByList = chatMessageService.getReadByForMessages(
                readMessageRequest.roomId(),
                PageRequest.of(pageValue, sizeValue)
        );

        messagingTemplate.convertAndSend("/topic/read/" + readMessageRequest.roomId(), ApiResponse.success((updatedReadByList)));
    }


}
