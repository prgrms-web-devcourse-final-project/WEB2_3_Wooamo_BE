package com.api.stuv.domain.socket.service;

import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.socket.dto.UserInfo;
import com.api.stuv.domain.socket.entity.ChatRoom;
import com.api.stuv.domain.socket.repository.ChatRoomRepository;
import com.api.stuv.domain.user.dto.ImageUrlDTO;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;

    @Autowired
    private final RedisTemplate<String, UserInfo> redisTemplate;
    private static final String USER_INFO_PREFIX = "user:info:";
    private static final Duration USER_INFO_CACHE_DURATION = Duration.ofMinutes(60);
    // 방별 사용자 목록 저장
    private final ConcurrentMap<String, Set<Long>> roomMembersCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 서버 시작 시 모든 채팅방의 멤버 정보를 불러와 roomMembersCache 초기화
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        for (ChatRoom chatRoom : chatRooms) {
            roomMembersCache.put(chatRoom.getRoomId(), new HashSet<>(chatRoom.getMembers()));
        }
    }

    // 특정 채팅방의 사용자 목록을 업데이트 (새로운 멤버 추가 시)
    public void updateRoomMembers(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        roomMembersCache.put(roomId, new HashSet<>(chatRoom.getMembers()));
    }

    // 읽음 처리 계산 (채팅방에 속한 사용자 수 반환)
    public int getRoomMemberCount(String roomId) {
        return roomMembersCache.getOrDefault(roomId, Collections.emptySet()).size();
    }

    // 사용자 정보 반환 및 정보 추가
    public UserInfo getUserInfo(Long userId) {
        String userKey = USER_INFO_PREFIX + userId;
        UserInfo userInfo = redisTemplate.opsForValue().get(userKey);
        if (userInfo == null) {
            userInfo = loadUserInfoFromDB(userId);
            redisTemplate.opsForValue().set(userKey, userInfo, USER_INFO_CACHE_DURATION);
        }
        return userInfo;
    }

    // DB에서 사용자 정보 불러와 Redis에 저장
    private UserInfo loadUserInfoFromDB(Long userId) {
        String nickname = (userId != null) ? userRepository.findNicknameByUserId(userId) : "";
        ImageUrlDTO response = (userId != null) ? userRepository.getCostumeInfoByUserId(userId) : null;
        String profileUrl = (response != null) ? s3ImageService.generateImageFile(EntityType.COSTUME, response.entityId(), response.newFileName()) : null;

        return new UserInfo(userId, nickname, profileUrl);
    }

    public void updateUserProfileInCache(Long userId, String newProfileUrl) {
        String userKey = USER_INFO_PREFIX + userId;

        UserInfo userInfo = redisTemplate.opsForValue().get(userKey);
        if (userInfo != null) {
            UserInfo updatedUserInfo = new UserInfo(userId, userInfo.nickname(), newProfileUrl);
            redisTemplate.opsForValue().set(userKey, updatedUserInfo, USER_INFO_CACHE_DURATION);
        }
    }

}
