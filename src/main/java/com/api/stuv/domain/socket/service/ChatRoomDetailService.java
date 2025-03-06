package com.api.stuv.domain.socket.service;

import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.socket.dto.ChatRoomResponse;
import com.api.stuv.domain.socket.dto.GroupInfo;
import com.api.stuv.domain.socket.dto.UserInfo;
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
    private final S3ImageService s3ImageService;



    public String createPrivateChatRoom(Long userId1, Long userId2) {
        List<Long> sortedIds = Arrays.asList(userId1, userId2);
        Collections.sort(sortedIds);
        String roomId = "PRIVATE_" + sortedIds.get(0) + "_" + sortedIds.get(1);

        boolean exists = chatRoomRepository.existsByRoomId(roomId);

        if (exists) {
            throw new NotFoundException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS);
        }

        List<Long> members = List.of(sortedIds.get(0), sortedIds.get(1));

        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(roomId)
                .roomType("PRIVATE")
                .members(members)
                .createdAt(LocalDateTime.now())
                .build();

        chatRoomRepository.save(chatRoom);

        return roomId;
    }

    public String createGroupChatRoom(String groupId, String groupName, Long userId, int maxMembers) {
        boolean exists = chatRoomRepository.existsByRoomId(groupName);

        if (exists) {
            throw new NotFoundException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS);
        }

        List<Long> members = new ArrayList<>();
        members.add(userId);

        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(String.valueOf(groupId))
                .roomType("GROUP")
                .roomName(groupName)
                .members(members)
                .maxMembers(maxMembers)
                .createdAt(LocalDateTime.now())
                .build();

        chatRoomRepository.save(chatRoom);

        return chatRoom.getRoomId();
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


    public List<ChatRoomResponse> getSortedRoomListBySenderId(Long senderId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByMembersContaining(senderId);

        if (chatRooms.isEmpty()) {
            return Collections.emptyList();
        }

        return chatRooms.stream()
                .map(room -> {
                    ChatMessage latestMessage = chatMessageRepository.findTopByRoomIdOrderByCreatedAtDesc(room.getRoomId());
                    int unreadCount = chatMessageRepository.countUnreadMessages(room.getRoomId(), senderId);

                    Long lastSenderId = (latestMessage != null) ? latestMessage.getSenderId() : null;
                    String lastSenderNickname = (lastSenderId != null) ? userRepository.findNicknameByUserId(lastSenderId) : null;
                    ImageUrlDTO response = (lastSenderId != null) ? userRepository.getCostumeInfoByUserId(lastSenderId) : null;
                    String lastSenderProfile = (response != null) ?
                            s3ImageService.generateImageFile(EntityType.COSTUME, response.entityId(), response.newFileName()) : null;

                    UserInfo lastUserInfo = new UserInfo(lastSenderId, lastSenderNickname, lastSenderProfile);

                    UserInfo userInfo = null;
                    GroupInfo groupInfo = null;

                    if ("PRIVATE".equals(room.getRoomType())) {
                        List<Long> members = room.getMembers();
                        Long otherUserId = members.stream()
                                .filter(id -> !id.equals(senderId))
                                .findFirst()
                                .orElse(null);

                        if (otherUserId != null) {
                            String nickName = userRepository.findNicknameByUserId(otherUserId);
                            ImageUrlDTO userResponse = userRepository.getCostumeInfoByUserId(otherUserId);
                            String profileImage = (userResponse != null) ?
                                    s3ImageService.generateImageFile(EntityType.COSTUME, userResponse.entityId(), userResponse.newFileName()) : null;

                            userInfo = new UserInfo(otherUserId, nickName, profileImage);
                        }
                    } else if ("GROUP".equals(room.getRoomType())) {
                        String groupId = room.getRoomId();
                        int totalMembers = room.getMembers().size();
                        String groupName = room.getRoomName();

                        groupInfo = new GroupInfo(groupId, groupName, totalMembers);
                    }

                    return ChatRoomResponse.from(room, lastUserInfo, userInfo, groupInfo, latestMessage, unreadCount);
                })
                .sorted(Comparator.comparing(ChatRoomResponse::createdAt, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

}
