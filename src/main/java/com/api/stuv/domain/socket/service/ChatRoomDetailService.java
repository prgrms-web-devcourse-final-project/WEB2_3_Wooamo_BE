package com.api.stuv.domain.socket.service;

import com.api.stuv.domain.party.repository.party.PartyGroupRepository;
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
    private final PartyGroupRepository partyGroupRepository;

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

                    if ("PRIVATE".equals(room.getRoomType())) {
                        String profileImageUrl = (latestMessage != null) ? userRepository.getCostumeInfoByUserId(latestMessage.getSenderId()) : null;
                        return ChatRoomResponse.from(room, latestMessage, profileImageUrl, null);
                    } else if ("GROUP".equals(room.getRoomType())) {
                        String groupName = partyGroupRepository.findPartyGroupNameByUserId(senderId);
                        return ChatRoomResponse.from(room, latestMessage, null, groupName);
                    }
                    return ChatRoomResponse.from(room, latestMessage, null, null);
                })
                .sorted(Comparator.comparing(ChatRoomResponse::createdAt, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
