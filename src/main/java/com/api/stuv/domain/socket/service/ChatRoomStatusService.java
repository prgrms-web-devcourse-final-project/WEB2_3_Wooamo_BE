package com.api.stuv.domain.socket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class ChatRoomStatusService {

    private final ConcurrentMap<String, Set<Long>> roomCache = new ConcurrentHashMap<>();
    private final Set<Long> listPageSubscribers = ConcurrentHashMap.newKeySet();

    // 방 입장 처리
    public void userEnterRoom(String roomId, Long userId) {
        roomCache.computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet()).add(userId);
    }

    // 방 퇴장 처리
    public void userExitRoom(String roomId, Long userId) {
        roomCache.computeIfPresent(roomId, (key, users) -> {
            users.remove(userId);
            return users.isEmpty() ? null : users;
        });
    }

    // 특정 방에 있는 사용자 가져오기
    public Set<Long> getUsersInRoom(String roomId) {
        return roomCache.getOrDefault(roomId, Collections.emptySet());
    }

    // 채팅 목록 페이지 구독
    public void subscribeListPage(Long userId) {
        listPageSubscribers.add(userId);
    }

    // 목록 페이지 구독 취소
    public void unsubscribeListPage(Long userId) {
        listPageSubscribers.remove(userId);
    }

    public Set<Long> getListPageSubscribers() {
        return Collections.unmodifiableSet(listPageSubscribers);
    }

}
