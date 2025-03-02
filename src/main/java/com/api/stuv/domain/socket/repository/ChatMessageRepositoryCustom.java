package com.api.stuv.domain.socket.repository;

public interface ChatMessageRepositoryCustom {
    void updateManyReadBy(String roomId, Long userId);
    int countUnreadMessages(String roomId, Long userId);

}
