package com.api.stuv.domain.socket.repository;

import com.api.stuv.domain.socket.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepositoryCustom {
    void updateManyReadBy(String roomId, Long userId);
    int countUnreadMessages(String roomId, Long userId);
    List<ChatMessage> findMessagesByRoomIdWithPagination(String roomId, String lastChatId, int limit);

}
