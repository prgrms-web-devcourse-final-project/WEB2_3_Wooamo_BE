package com.api.stuv.domain.socket.service;

import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.party.repository.party.PartyGroupRepository;
import com.api.stuv.domain.socket.dto.ChatRoomResponse;
import com.api.stuv.domain.socket.entity.ChatMessage;
import com.api.stuv.domain.socket.entity.ChatRoom;
import com.api.stuv.domain.socket.repository.ChatMessageRepository;
import com.api.stuv.domain.socket.repository.ChatRoomRepository;
import com.api.stuv.domain.user.dto.ImageUrlDTO;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomDetailService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final PartyGroupRepository partyGroupRepository;
    private final S3ImageService s3ImageService;


    public List<ChatRoomResponse> getSortedRoomListBySenderId(Long senderId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByMembersContaining(senderId);

        if (chatRooms.isEmpty()) {
            throw new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        return chatRooms.stream()
                .map(room -> {
                    ChatMessage latestMessage = chatMessageRepository.findTopByRoomIdOrderByCreatedAtDesc(room.getRoomId());
                    int unreadCount = chatMessageRepository.countUnreadMessages(room.getRoomId(), senderId);
                    ImageUrlDTO response = userRepository.getCostumeInfoByUserId(latestMessage.getSenderId());

                    String profile = ("PRIVATE".equals(room.getRoomType()) && latestMessage != null)
                            ? s3ImageService.generateImageFile(EntityType.COSTUME, response.entityId(), response.newFileName())
                            : null;

                    return ChatRoomResponse.from(room, latestMessage, profile, unreadCount);
                })
                .sorted(Comparator.comparing(ChatRoomResponse::createdAt, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public String createPrivateChatRoom(Long userId1, Long userId2) {
        List<Long> sortedIds = Arrays.asList(userId1, userId2);
        Collections.sort(sortedIds);
        String roomId = "PRIVATE_" + sortedIds.get(0) + "_" + sortedIds.get(1);

        boolean exists = chatRoomRepository.existsByRoomId(roomId);

        if (exists) {
            throw new NotFoundException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS);
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(roomId)
                .roomType("PRIVATE")
                .members(sortedIds)
                .createdAt(LocalDateTime.now())
                .build();

        chatRoomRepository.save(chatRoom);

        return roomId;
    }

    public String createGroupChatRoom(String groupName, Long userId, int maxMembers) {
        String roomId = groupName;
        System.out.println(roomId);
        boolean exists = chatRoomRepository.existsByRoomId(roomId);

        if (exists) {
            throw new NotFoundException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS);
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(roomId)
                .roomType("GROUP")
                .roomName(groupName)
                .members(new ArrayList<>(List.of(userId)))
                .maxMembers(maxMembers)
                .createdAt(LocalDateTime.now())
                .build();

        chatRoomRepository.save(chatRoom);

        return roomId;
    }

    public void addUserToGroupChat(String roomId, Long newUserId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        List<Long> members = chatRoom.getMembers();
        if (members.contains(newUserId)) {
            throw new NotFoundException(ErrorCode.USER_ALREADY_IN_CHAT_ROOM);
        }

        if (members.size() >= chatRoom.getMaxMembers()) {
            throw new NotFoundException(ErrorCode.CHAT_ROOM_MAX_MEMBERS_EXCEEDED);
        }

        members.add(newUserId);
        chatRoomRepository.save(chatRoom);
    }

    public void deleteChatRoom(String roomId) {
        boolean exists = chatRoomRepository.existsByRoomId(roomId);
        if (!exists) {
            throw new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
        chatRoomRepository.deleteByRoomId(roomId);

        chatMessageRepository.deleteByRoomId(roomId);
    }
}
