package com.api.stuv.domain.socket.repository;

import com.api.stuv.domain.socket.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String>{
    Optional<ChatRoom> findByRoomId(String roomId);
    List<ChatRoom> findByMembersContaining(Long userId);
}
