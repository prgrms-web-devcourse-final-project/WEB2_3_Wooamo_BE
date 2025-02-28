package com.api.stuv.domain.socket.service;

import com.api.stuv.domain.socket.entity.ChatRoom;
import com.api.stuv.domain.socket.repository.ChatRoomRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberService {
    // 방 이름(roomId)별로 멤버 ID 목록을 저장하는 ConcurrentMap
    private final ConcurrentMap<String, List<Long>> roomSessions = new ConcurrentHashMap<>();

    private final ChatRoomRepository chatRoomRepository;

    @PostConstruct
    public void init() {
        // 서버 시작 시 모든 채팅방의 멤버 정보를 불러와 roomSessions 초기화
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        for (ChatRoom chatRoom : chatRooms) {
            roomSessions.put(chatRoom.getRoomId(), new ArrayList<>(chatRoom.getMembers()));
        }
    }

    public void updateRoomMembers(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));        List<Long> members = chatRoom.getMembers();
        roomSessions.put(roomId, new ArrayList<>(members));
    }

    public int getRoomMemberCount(String roomId) {
        List<Long> members = roomSessions.get(roomId);
        return (members != null) ? members.size() : 0;
    }
}
