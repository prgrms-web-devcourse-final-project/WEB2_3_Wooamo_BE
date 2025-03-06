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

        // lastChatId가 존재하면, 해당 _id보다 작은 메시지만 조회 (즉, 이전 메시지)
        if (lastChatId != null && !lastChatId.isEmpty()) {
            query.addCriteria(Criteria.where("_id").lt(new ObjectId(lastChatId)));
        }

        // 최신 메시지부터 정렬
        query.with(Sort.by(Sort.Direction.DESC, "created_at"));
        query.limit(limit);

        return mongoTemplate.find(query, ChatMessage.class);
    }

//    @Override
//    public List<ChatMessage> findMessagesByRoomIdWithPagination(String roomId, String lastChatId, int limit) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("room_id").is(roomId));
//
//        // lastChatId가 존재하면 해당 ID 포함하여 limit개 가져오기
//        if (lastChatId != null && !lastChatId.isEmpty()) {
//            query.addCriteria(Criteria.where("_id").lte(new ObjectId(lastChatId))); // lastChatId 포함
//        }
//
//        // 최신순 정렬 후 limit 개수 가져오기
//        query.with(Sort.by(Sort.Direction.DESC, "created_at"));
//        query.limit(limit);
//
//        List<ChatMessage> messages = mongoTemplate.find(query, ChatMessage.class);
//
//        // lastChatId가 없을 때 최신 메시지 limit개 가져오기
//        if (lastChatId == null || lastChatId.isEmpty()) {
//            messages = mongoTemplate.find(query, ChatMessage.class);
//        }
//
//        return messages;
//    }

}
