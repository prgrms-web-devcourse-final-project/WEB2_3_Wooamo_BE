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
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberService {

    // 방 이름(roomId)별로 모든 멤버 ID 목록을 저장하는 -> ReadBy
//    private final ConcurrentMap<String, List<Long>> roomTotalMembersCache = new ConcurrentHashMap<>();
    // 사용자 정보 저장 -> UserInfo
//    private final ConcurrentMap<String, Set<Long>> userRoomCache = new ConcurrentHashMap<>();
//    private final ConcurrentMap<Long, UserInfo> userInfoCache = new ConcurrentHashMap<>();

    // 방별 사용자 목록 저장
    private final ConcurrentMap<String, Set<Long>> roomMembersCache = new ConcurrentHashMap<>();
    // 사용자 정보 저장
    private final ConcurrentMap<Long, UserInfo> userInfoCache = new ConcurrentHashMap<>();

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;

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

    // 사용자가 방에 입장하면 roomMembersCache와 userInfoCache에 정보 추가
    public void userJoinRoom(Long userId, String roomId) {
        // 방 정보가 캐시에 없으면 DB에서 불러와 등록
        roomMembersCache.computeIfAbsent(roomId, this::loadRoomMembersFromDB);

        // 방에 사용자 추가
        roomMembersCache.get(roomId).add(userId);

        // 방의 모든 사용자 정보 캐싱
        roomMembersCache.get(roomId).forEach(memberId -> {
            userInfoCache.computeIfAbsent(memberId, this::loadUserInfoFromDB);
        });

    }
    
    /*Todo 레디스에 TTL 걸고 userLeaveRoom는 삭제*/
    // 사용자가 특정 방에서 나가면 roomMembersCache에서 제거
    public void userLeaveRoom(Long userId, String roomId) {
//        Set<Long> members = roomMembersCache.get(roomId);
//        if (members != null) {
//            members.remove(userId);
//            if (members.isEmpty()) {
//                roomMembersCache.remove(roomId); // 방에 아무도 없으면 캐시에서 제거
//            }
//
//        }

//         사용자가 아직 다른 방에 남아 있는지 확인 후 정보 제거
        boolean isUserStillInAnyRoom = roomMembersCache.values().stream()
                .anyMatch(memberSet -> memberSet.contains(userId));

        if (!isUserStillInAnyRoom) {
            userInfoCache.remove(userId);
        }
    }

    // 사용자 정보 반환
    public UserInfo getUserInfo(Long userId) {
        return userInfoCache.getOrDefault(userId, null);
    }

    // 특정 채팅방의 사용자 목록 반환
    public List<UserInfo> getRoomMemberInfos(String roomId) {
        Set<Long> userIds = roomMembersCache.getOrDefault(roomId, Collections.emptySet());

        return userIds.stream()
                .map(userInfoCache::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // DB에서 특정 채팅방 멤버 목록을 불러와 캐시에 저장
    private Set<Long> loadRoomMembersFromDB(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return new HashSet<>(chatRoom.getMembers());
    }

    // DB에서 사용자 정보 불러와 캐시에 저장
    private UserInfo loadUserInfoFromDB(Long userId) {
        String nickname = (userId != null) ? userRepository.findNicknameByUserId(userId) : "";

        ImageUrlDTO response = (userId != null) ? userRepository.getCostumeInfoByUserId(userId) : null;
        String profileUrl = (response != null) ? s3ImageService.generateImageFile(EntityType.COSTUME, response.entityId(), response.newFileName()) : null;

        return new UserInfo(userId, nickname, profileUrl);
    }

//    @PostConstruct
//    public void init() {
//        // 서버 시작 시 모든 채팅방의 멤버 정보를 불러와 roomSessions 초기화
//        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
//        for (ChatRoom chatRoom : chatRooms) {
//            roomTotalMembersCache.put(chatRoom.getRoomId(), new ArrayList<>(chatRoom.getMembers()));
//        }
//    }
//
//    // 특정 채팅방의 사용자 목록을 업데이트 (새로운 멤버 추가 시)
//    public void updateRoomMembers(String roomId) {
//        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
//                .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));
//        List<Long> members = chatRoom.getMembers();
//        roomTotalMembersCache.put(roomId, new ArrayList<>(members));
//    }
//    // 읽음 처리 계산
//    public int getRoomMemberCount(String roomId) {
//        List<Long> members = roomTotalMembersCache.get(roomId);
//        return (members != null) ? members.size() : 0;
//    }

//  // 특정 사용자가 현재 참여 중인 방 개수 반환
//    public int getUserActiveRoomCount(Long userId) {
//        return (int) roomTotalMembersCache.values().stream()
//                .filter(members -> members.contains(userId))
//                .count();
//    }
//    // 특정 채팅방의 사용자 목록 반환
//    public List<Long> getRoomMembers(String roomId) {
//        return roomTotalMembersCache.getOrDefault(roomId, List.of());
//    }











//    // 사용자가 방에 입장하면 userSessions에 사용자 정보를 추가
//    public void userJoinRoom(Long userId, String roomId) {
//        userRoomCache.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
//
//        // 방의 모든 사용자 정보 추가
//        userRoomCache.get(roomId).forEach(memberId -> {
//            userInfoCache.computeIfAbsent(memberId, id -> {
//                String nickname = (id != null) ? userRepository.findNicknameByUserId(id) : "";
//
//                ImageUrlDTO response = (id != null) ? userRepository.getCostumeInfoByUserId(id) : null;
//                String profileUrl = (response != null) ? s3ImageService.generateImageFile(EntityType.COSTUME, response.entityId(), response.newFileName()) : null;
//
//                return new UserInfo(id, nickname, profileUrl);
//            });
//        });
//
//    }
//    // 사용자가 특정 방에서 나가면 userSessions에서 제거
//    public void userLeaveRoom(Long userId, String roomId) {
//        Set<Long> members = userRoomCache.get(roomId);
//        if (members != null) {
//            members.remove(userId);
//            if (members.isEmpty()) {
//                userRoomCache.remove(roomId);
//            }
//        }
//
//        // 사용자가 아직 다른 방에 남아 있는지 확인 후 제거
//        boolean isUserStillInAnyRoom = userRoomCache.values().stream()
//                .anyMatch(memberSet -> memberSet.contains(userId));
//
//        if (!isUserStillInAnyRoom) {
//            userInfoCache.remove(userId);
//        }
//
//    }
//    // 사용자 정보 반환
//    public UserInfo getUserInfo(Long userId) {
//        return userInfoCache.getOrDefault(userId, null);
//    }
//
//    // 특정 채팅방의 사용자 목록 반환
//    public List<UserInfo> getRoomMemberInfos(String roomId) {
//        Set<Long> userIds = userRoomCache.getOrDefault(roomId, Collections.emptySet());
//
//        return userIds.stream()
//                .map(userInfoCache::get)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }

}
