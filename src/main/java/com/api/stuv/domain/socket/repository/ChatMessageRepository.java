package com.api.stuv.domain.socket.repository;

import com.api.stuv.domain.socket.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
}
