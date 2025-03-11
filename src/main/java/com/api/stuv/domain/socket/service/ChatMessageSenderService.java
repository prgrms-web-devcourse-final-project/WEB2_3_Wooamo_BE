package com.api.stuv.domain.socket.service;

import com.api.stuv.domain.socket.dto.ChatMessageResponse;
import com.api.stuv.domain.socket.dto.ChatRoomResponse;
import com.api.stuv.domain.socket.dto.ChatRoomTypeInfoResponse;
import com.api.stuv.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatMessageSenderService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomDetailService chatRoomDetailService;
    private final ChatRoomStatusService chatRoomStatusService;

    // 사용자 입장 메시지 발송
    public void sendUserJoinedMessage(String roomId, ChatRoomTypeInfoResponse chatRoomTypeInfoResponse, Long userId) {
        messagingTemplate.convertAndSend(
                "/topic/users/" + roomId,
                ApiResponse.success(chatRoomTypeInfoResponse)
        );
        messagingTemplate.convertAndSend(
                "/topic/read/" + roomId,
                ApiResponse.success(Map.of("userId", userId))
        );
    }

    // 특정 사용자에게 업데이트된 방 목록 전송
    public void sendUpdatedRoomList(Long userId) {
        List<ChatRoomResponse> roomList = chatRoomDetailService.getSortedRoomListBySenderId(userId);
        messagingTemplate.convertAndSend(
                "/topic/rooms/" + userId,
                ApiResponse.success(roomList)
        );
    }

    // 채팅방에 메시지 발송
    public void sendMessageToRoom(String roomId, ChatMessageResponse message) {
        messagingTemplate.convertAndSend(
                "/topic/messages/" + roomId,
                ApiResponse.success(message)
        );
    }

    // 구독자들에게 방 목록 갱신 알림
    public void notifyRoomListSubscribers() {
        chatRoomStatusService.getListPageSubscribers().forEach(subscriberId -> {
            sendUpdatedRoomList(subscriberId);
        });
    }


}
