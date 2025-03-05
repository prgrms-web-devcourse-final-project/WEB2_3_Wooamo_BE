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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;
    private final ChatRoomMemberService chatRoomMemberService; // 총 멤버 정보를 관리하는 서비스

    //메세지 불러오기
    public List<ChatMessageResponse> getMessagesByRoomId(String roomId, Pageable pageable) {

        List<ChatMessageResponse> messages = chatMessageRepository
                .findByRoomIdOrderByCreatedAtDesc(roomId, pageable)
                .getContent()
                .stream()
                .map(chatMessage -> {
                    int unreadCount = chatRoomMemberService.getRoomMemberCount(chatMessage.getRoomId()) - chatMessage.getReadBy().size();
                    UserInfo userInfo = chatRoomMemberService.getUserInfo(chatMessage.getSenderId());
                    if (userInfo == null) {
                        Long senderId = chatMessage.getSenderId();
                        String senderNickname = (senderId != null) ? userRepository.findNicknameByUserId(senderId) : "";

                        ImageUrlDTO response = (senderId != null) ? userRepository.getCostumeInfoByUserId(senderId) : null;
                        String senderProfile = (response != null) ? s3ImageService.generateImageFile(EntityType.COSTUME, response.entityId(), response.newFileName()) : null;

                        userInfo = new UserInfo(senderId, senderNickname, senderProfile);
                    }

                    return new ChatMessageResponse(
                            chatMessage.getId(),
                            chatMessage.getRoomId(),
                            userInfo,
                            chatMessage.getMessage(),
                            unreadCount,
                            chatMessage.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());

        Collections.reverse(messages);

        return messages;
    }

    // 메시지 저장
    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request) {
        chatRoomRepository.findByRoomId(request.roomId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        UserInfo userInfo = chatRoomMemberService.getUserInfo(request.userInfo().userId());

        ChatMessage savedMessage = chatMessageRepository.save(
                ChatMessage.builder()
                        .roomId(request.roomId())
                        .senderId(request.userInfo().userId())
                        .message(request.message())
                        .readBy(request.readBy())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // 기본적으로 DB에 저장된 readByCount (읽은 인원 수)
        int unreadCount = chatRoomMemberService.getRoomMemberCount(request.roomId()) - savedMessage.getReadBy().size();

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
