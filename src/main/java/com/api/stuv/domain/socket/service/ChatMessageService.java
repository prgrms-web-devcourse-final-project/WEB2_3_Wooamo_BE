package com.api.stuv.domain.socket.service;

import com.api.stuv.domain.socket.dto.ChatMessageRequestDto;
import com.api.stuv.domain.socket.dto.ChatMessageResponseDto;
import com.api.stuv.domain.socket.entity.ChatMessage;
import com.api.stuv.domain.socket.entity.ChatRoom;
import com.api.stuv.domain.socket.repository.ChatMessageRepository;
import com.api.stuv.domain.socket.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    public List<ChatMessageResponseDto> getMessagesByRoomIdPagination(String roomId, int page, int size) {
        Pageable pageable = PageRequest.of(0, 10);
        List<ChatMessageResponseDto> messages = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable)
                .getContent().stream()
                .map(ChatMessageResponseDto::fromEntity)
                .collect(Collectors.toList());

        Collections.reverse(messages);

        return messages;
    }

    // 메시지 저장
    @Transactional
    public ChatMessage saveMessage(ChatMessageRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(requestDto.getRoomId());
        if (chatRoom == null) {
            throw new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + requestDto.getRoomId());
        }

        ChatMessage savedMessage = chatMessageRepository.save(
                ChatMessage.builder()
                        .roomId(requestDto.getRoomId())
                        .senderId(requestDto.getSenderId())
                        .message(requestDto.getMessage())
                        .readBy(requestDto.getReadBy())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return savedMessage;
    }

    // 특정 senderId가 포함된 채팅방의 roomId 목록 조회
    public List<String> getRoomIdsBySenderId(Long senderId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByMembersContaining(senderId);
        return chatRooms.stream()
                .map(ChatRoom::getRoomId)
                .distinct()
                .collect(Collectors.toList());
    }

    // roomId에 해당하는 메시지들을 읽음 상태로 업데이트
    @Transactional
    public void markMessagesAsRead(String roomId, Long userId) {
        chatMessageRepository.updateManyReadBy(roomId, userId);
    }

}
