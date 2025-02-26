package com.api.stuv.domain.socket.service;

import com.api.stuv.domain.socket.dto.ChatMessageRequest;
import com.api.stuv.domain.socket.dto.ChatMessageResponse;
import com.api.stuv.domain.socket.entity.ChatMessage;
import com.api.stuv.domain.socket.entity.ChatRoom;
import com.api.stuv.domain.socket.repository.ChatMessageRepository;
import com.api.stuv.domain.socket.repository.ChatRoomRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    
    //메세지 불러오기
    public List<ChatMessageResponse> getMessagesByRoomIdPagination(String roomId, Pageable pageable) {
        List<ChatMessageResponse> messages = chatMessageRepository
                .findByRoomIdOrderByCreatedAtDesc(roomId, pageable)
                .getContent()
                .stream()
                .map(ChatMessageResponse::from)
                .collect(Collectors.toList());

        Collections.reverse(messages);

        return messages;
    }

    // 메시지 저장
    @Transactional
    public ChatMessage saveMessage(ChatMessageRequest request) {
        chatRoomRepository.findByRoomId(request.roomId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        return chatMessageRepository.save(
                ChatMessage.builder()
                        .roomId(request.roomId())
                        .senderId(request.senderId())
                        .message(request.message())
                        .readBy(request.readBy())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    // 특정 senderId가 포함된 채팅방의 roomId 목록 조회
    public List<String> getRoomIdsBySenderId(Long senderId) {
        List<String> roomIds = chatRoomRepository.findByMembersContaining(senderId)
                .stream()
                .map(ChatRoom::getRoomId)
                .distinct()
                .collect(Collectors.toList());

        if (roomIds.isEmpty()) {
            throw new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        return roomIds;
    }

    // roomId에 해당하는 메시지들을 읽음 상태로 업데이트
    @Transactional
    public void markMessagesAsRead(String roomId, Long userId) {
        chatMessageRepository.updateManyReadBy(roomId, userId);
    }

}
