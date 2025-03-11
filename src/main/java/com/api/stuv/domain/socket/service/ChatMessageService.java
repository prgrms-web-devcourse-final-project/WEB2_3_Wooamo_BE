package com.api.stuv.domain.socket.service;

import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.socket.dto.ChatMessageRequest;
import com.api.stuv.domain.socket.dto.ChatMessageResponse;
import com.api.stuv.domain.socket.dto.ReadByResponse;
import com.api.stuv.domain.socket.dto.UserInfo;
import com.api.stuv.domain.socket.entity.ChatMessage;
import com.api.stuv.domain.socket.repository.ChatMessageRepository;
import com.api.stuv.domain.socket.repository.ChatRoomRepository;
import com.api.stuv.domain.user.dto.ImageUrlDTO;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomStatusService chatRoomStatusService;
    private final ChatRoomMemberService chatRoomMemberService;
    
    //메세지 불러오기
    public List<ChatMessageResponse> getMessagesByRoomId(String roomId, String lastChatId, int limit) {
        List<ChatMessage> messages = chatMessageRepository.findMessagesByRoomIdWithPagination(roomId, lastChatId, limit);
        int totalMembers = chatRoomMemberService.getRoomMemberCount(roomId);
        Collections.reverse(messages);


        return messages.stream()
                .map(chatMessage -> {
                    Long senderId = chatMessage.getSenderId();

                    UserInfo userInfo = chatRoomMemberService.getUserInfo(senderId);

                    int readByCount = (chatMessage.getReadBy() != null) ? chatMessage.getReadBy().size() : 0;

                    return ChatMessageResponse.from(chatMessage, userInfo, totalMembers - readByCount);
                })
                .collect(Collectors.toList());
    }

    //특정 메시지(lastChatId)까지 조회
    public List<ChatMessageResponse> getMessagesUntilLastChatId(String roomId, String lastChatId) {
        if (lastChatId == null) {
            return Collections.emptyList();
        }

        List<ChatMessage> messages = chatMessageRepository.findMessagesUntilLastChatId(roomId, lastChatId);

        if (messages.isEmpty()) {
            return Collections.emptyList();
        }

        int totalMembers = chatRoomMemberService.getRoomMemberCount(roomId);
        Collections.reverse(messages);

        return messages.stream()
                .map(chatMessage -> {
                    Long senderId = chatMessage.getSenderId();
                    UserInfo userInfo = chatRoomMemberService.getUserInfo(senderId);
                    int readByCount = (chatMessage.getReadBy() != null) ? chatMessage.getReadBy().size() : 0;

                    return ChatMessageResponse.from(chatMessage, userInfo, totalMembers - readByCount);
                })
                .collect(Collectors.toList());
    }

    // 메시지 저장
    @Transactional
    public ChatMessageResponse saveMessageWithReadBy(ChatMessageRequest request) {
        Set<Long> usersInRoom = chatRoomStatusService.getUsersInRoom(request.roomId());

        ChatMessage savedMessage = chatMessageRepository.save(
                ChatMessage.builder()
                        .roomId(request.roomId())
                        .senderId(request.userInfo().userId())
                        .message(request.message())
                        .readBy(new ArrayList<>(usersInRoom)) // 읽은 사람 처리
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        UserInfo userInfo = chatRoomMemberService.getUserInfo(request.userInfo().userId());
        int unreadCount = chatRoomMemberService.getRoomMemberCount(request.roomId()) - usersInRoom.size();

        return new ChatMessageResponse(
                savedMessage.getId(),
                savedMessage.getRoomId(),
                userInfo,
                savedMessage.getMessage(),
                unreadCount,
                savedMessage.getCreatedAt()
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
