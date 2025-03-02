package com.api.stuv.domain.socket.service;

import com.api.stuv.domain.socket.dto.ChatMessageRequest;
import com.api.stuv.domain.socket.dto.ChatMessageResponse;
import com.api.stuv.domain.socket.dto.ReadByResponse;
import com.api.stuv.domain.socket.entity.ChatMessage;
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
    private final ChatRoomMemberService chatRoomMemberService; // 총 멤버 정보를 관리하는 서비스

    //메세지 불러오기
    public List<ChatMessageResponse> getMessagesByRoomIdPagination(String roomId, Pageable pageable) {

        List<ChatMessageResponse> messages = chatMessageRepository
                .findByRoomIdOrderByCreatedAtDesc(roomId, pageable)
                .getContent()
                .stream()
                .map(chatMessage -> {
                    int unreadCount = chatRoomMemberService.getRoomMemberCount(chatMessage.getRoomId()) - chatMessage.getReadBy().size();

                    return new ChatMessageResponse(
                            chatMessage.getId(),
                            chatMessage.getRoomId(),
                            chatMessage.getSenderId(),
                            chatMessage.getMessage(),
                            unreadCount
                    );
                })
                .collect(Collectors.toList());

        Collections.reverse(messages);

        return messages;
    }

    // 메시지 저장
    @Transactional
    public ChatMessageResponse  saveMessage(ChatMessageRequest request) {
        chatRoomRepository.findByRoomId(request.roomId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        ChatMessage savedMessage = chatMessageRepository.save(
                ChatMessage.builder()
                        .roomId(request.roomId())
                        .senderId(request.senderId())
                        .message(request.message())
                        .readBy(request.readBy())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // 기본적으로 DB에 저장된 readByCount (읽은 인원 수)
        ChatMessageResponse response = ChatMessageResponse.from(savedMessage);
        int unreadCount = chatRoomMemberService.getRoomMemberCount(request.roomId()) - response.readByCount();
        return new ChatMessageResponse(
                response.chatId(),
                response.roomId(),
                response.senderId(),
                response.message(),
                unreadCount
        );
    }

    // roomId에 해당하는 메시지들을 읽음 상태로 업데이트
    @Transactional
    public void markMessagesAsRead(String roomId, Long userId) {
        chatMessageRepository.updateManyReadBy(roomId, userId);
    }

    // 읽음 처리된 메시지들의 readBy 리스트만 반환
    @Transactional
    public List<ReadByResponse> getReadByForMessages(String roomId, Pageable pageable) {
        int totalMembers = chatRoomMemberService.getRoomMemberCount(roomId);

        List<ReadByResponse> responses = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable)
                .getContent()
                .stream()
                .map(ReadByResponse::from)
                .map(r -> new ReadByResponse(
                        r.chatId(),
                        r.roomId(),
                        totalMembers - r.readByCount() // unreadCount 계산
                ))
                .collect(Collectors.toList());

        Collections.reverse(responses);

        return responses;
    }

}
