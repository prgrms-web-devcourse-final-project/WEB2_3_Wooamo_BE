package com.api.stuv.domain.socket.controller;


import com.api.stuv.domain.socket.dto.*;

import com.api.stuv.domain.socket.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Tag(name = "ChatMessage", description = "메세지 관련 WebSocket API")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final ChatRoomDetailService chatRoomDetailService;
    private final ChatRoomMemberService chatRoomMemberService;
    private final ChatRoomStatusService chatRoomStatusService;
    private final ChatMessageSenderService chatMessageSenderService; // -> chatMessageSenderService
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageController.class);

    /**
     * 사용자 채팅방 입장 처리
     * - /topic/users/{roomId}: 방 정보 업데이트
     * - /topic/read/{roomId}: 읽음 처리 업데이트
     */
    @Operation(summary = "채팅방 입장", description = "사용자가 채팅방에 입장할 때 등록합니다.")
    @MessageMapping("/chat/join")
    public void addUserToRoom(@Payload ReadMessageRequest request) {
        chatRoomMemberService.getUserInfo(request.userId());
        chatRoomStatusService.userEnterRoom(request.roomId(), request.userId());

        logger.info("{} 사용자가 채팅방 {}에 입장", request.userId(), request.roomId());

        chatMessageService.markMessagesAsRead(request.roomId(), request.userId());

        ChatRoomTypeInfoResponse typeInfoResponse = chatRoomDetailService.getChatRoomInfoByRoomName(request.userId(), request.roomId());
        chatMessageSenderService.sendUserJoinedMessage(request.roomId(), typeInfoResponse, request.userId());
    }

    /**
     * 사용자 채팅방 퇴장 처리
     * - 전송 URL 없음 (서버에서 사용자 상태만 제거)
     */
    @Operation(summary = "채팅방 퇴장", description = "사용자가 채팅방에서 나갈 때 삭제합니다.")
    @MessageMapping("/chat/leave")
    public void removeUserFromRoom(@Payload ReadMessageRequest request) {
        chatRoomStatusService.userExitRoom(request.roomId(), request.userId());
        logger.info("{} 사용자가 채팅방 {}에서 나감", request.userId(), request.roomId());
    }

    /**
     * 사용자 채팅 목록 페이지 입장 처리
     * - /topic/rooms/{userId}: 최신 채팅방 목록 갱신하여 전달
     */
    @Operation(summary = "채팅 목록 페이지 입장", description = "사용자가 채팅 목록 페이지에 들어올 때 등록합니다.")
    @MessageMapping("/chat/list/join")
    public void addUserToListPage(@Payload ReadMessageRequest request) {
        chatRoomStatusService.subscribeListPage(request.userId());
        chatMessageSenderService.sendUpdatedRoomList(request.userId());
    }

    /**
     * 사용자 채팅 목록 페이지 퇴장 처리
     * - 전송 URL 없음 (서버에서 사용자 상태만 제거)
     */
    @Operation(summary = "채팅 목록 페이지 퇴장", description = "사용자가 채팅 목록 페이지를 나갈 때 제거합니다.")
    @MessageMapping("/chat/list/leave")
    public void removeUserFromListPage(@Payload ReadMessageRequest request) {
        logger.info("{} 사용자가 채팅방 목록 페이지에서 나갔습니다.", request.userId());
        chatRoomStatusService.unsubscribeListPage(request.userId());
    }

    /**
     * 채팅방 메시지 전송 처리
     * - /topic/messages/{roomId}: 메시지를 채팅방 구독자에게 전송
     * - /topic/rooms/{userId}: 채팅 목록 구독자에게 최신 채팅방 목록 갱신하여 전달
     */
    @Operation(summary = "메시지 전송", description = "채팅방에 메시지를 전송합니다.")
    @MessageMapping("/chat/send")
    public void sendMessage(@Payload ChatMessageRequest message) {

        logger.info("메시지 전송 [{} -> {}]: {}", message.userInfo().userId(), message.roomId(), message.message());
        ChatMessageResponse savedMessage = chatMessageService.saveMessageWithReadBy(message);
        chatMessageSenderService.sendMessageToRoom(message.roomId(), savedMessage);
        chatMessageSenderService.notifyRoomListSubscribers();
    }

}
