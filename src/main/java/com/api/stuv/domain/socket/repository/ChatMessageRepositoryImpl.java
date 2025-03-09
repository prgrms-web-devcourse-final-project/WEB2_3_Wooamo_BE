package com.api.stuv.domain.socket.repository;

import com.api.stuv.domain.socket.entity.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

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
        Query query = new Query(where("room_id").is(roomId).and("read_by.userId").ne(userId));
        Update update = new Update().addToSet("read_by", userId);

        mongoTemplate.updateMulti(query, update, ChatMessage.class);
    }
    
    //사용자가 안읽은 메세지 갯수
    @Override
    public int countUnreadMessages(String roomId, Long userId) {
        Query query = new Query()
                .addCriteria(Criteria.where("room_id").is(roomId))
                .with(Sort.by(Sort.Direction.DESC, "created_at"))
                .limit(100);

        int unreadCount = 0;
        try (Stream<ChatMessage> stream = mongoTemplate.stream(query, ChatMessage.class)) {
            Iterator<ChatMessage> iterator = stream.iterator();
            while (iterator.hasNext()) {
                ChatMessage message = iterator.next();
                if (message.getReadBy().contains(userId)) {
                    break;
                }
                unreadCount++;
            }
        }
        return unreadCount;
    }

    @Override
    public List<ChatMessage> findMessagesByRoomIdWithPagination(String roomId, String lastChatId, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("room_id").is(roomId));

        if (lastChatId != null && !lastChatId.isEmpty()) {
            query.addCriteria(Criteria.where("_id").lt(new ObjectId(lastChatId)));
        }

        query.with(Sort.by(Sort.Direction.DESC, "created_at"));
        query.limit(limit);

        return mongoTemplate.find(query, ChatMessage.class);
    }

    public List<ChatMessage> findMessagesUntilLastChatId(String roomId, String lastChatId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));

        query.addCriteria(Criteria.where("_id").gte(new ObjectId(lastChatId)));

        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));

        return mongoTemplate.find(query, ChatMessage.class);
    }
}
