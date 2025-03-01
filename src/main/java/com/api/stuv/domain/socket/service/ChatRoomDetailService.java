package com.api.stuv.domain.socket.service;

import com.api.stuv.domain.socket.dto.ChatRoomResponse;
import com.api.stuv.domain.socket.entity.ChatMessage;
import com.api.stuv.domain.socket.entity.ChatRoom;
import com.api.stuv.domain.socket.repository.ChatMessageRepository;
import com.api.stuv.domain.socket.repository.ChatRoomRepository;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomDetailService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    // 특정 senderId가 포함된 채팅방의 roomId 목록 조회
    public List<String> getRoomIdsBySenderId(Long senderId, Pageable pageable) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findByMembersContaining(senderId, pageable);

        if (chatRooms.isEmpty()) {
            throw new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        return chatRooms.getContent()
                .stream()
                .map(ChatRoom::getRoomId)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<ChatRoomResponse> getSortedRoomListBySenderId(Long senderId, Pageable pageable) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findByMembersContaining(senderId, pageable);

        if (chatRooms.isEmpty()) {
            throw new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        return chatRooms.getContent()
                .stream()
                .map(room -> {
                    ChatMessage latestMessage = chatMessageRepository.findTopByRoomIdOrderByCreatedAtDesc(room.getRoomId());

                    String profileImageUrl = null;
                    if ("PRIVATE".equals(room.getRoomType()) && latestMessage != null) {
                        profileImageUrl = userRepository.getCostumeInfoByUserId(latestMessage.getSenderId());
                    }

                    return ChatRoomResponse.from(room, latestMessage, profileImageUrl);
                })
                .sorted(Comparator.comparing(ChatRoomResponse::createdAt, Comparator.reverseOrder())) // 최신 메시지 기준 내림차순 정렬
                .collect(Collectors.toList());
    }
}
