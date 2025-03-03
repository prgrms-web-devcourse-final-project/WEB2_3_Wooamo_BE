package com.api.stuv.domain.socket.repository;

import com.api.stuv.domain.socket.entity.ChatMessage;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String>, ChatMessageRepositoryCustom {
    Page<ChatMessage> findByRoomIdOrderByCreatedAtDesc(String roomId, Pageable pageable);
    ChatMessage findTopByRoomIdOrderByCreatedAtDesc(String roomId);
    void deleteByRoomId(String roomId);
}
