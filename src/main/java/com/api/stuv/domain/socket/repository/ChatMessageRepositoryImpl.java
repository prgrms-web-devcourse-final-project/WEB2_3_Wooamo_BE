package com.api.stuv.domain.socket.repository;

import com.api.stuv.domain.socket.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class ChatMessageRepositoryImpl implements ChatMessageRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ChatMessageRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void updateManyReadBy(String roomId, Long userId) {
        Query query = new Query(where("room_id").is(roomId).and("read_by").ne(userId)); // 읽지 않은 메시지만 선택
        Update update = new Update().addToSet("read_by", userId); // read_by 배열에 userId 추가

        mongoTemplate.updateMulti(query, update, ChatMessage.class); // 여러 개의 메시지 업데이트
    }
}
