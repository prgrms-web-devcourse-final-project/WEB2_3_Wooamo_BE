package com.api.stuv.domain.socket.entity;


import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "chat_room") // MongoDB 컬렉션 이름 지정
public class ChatRoom {

    @Id
    private String id;

    @Field("room_id")
    private String roomId;

    @Field("room_type")
    private String roomType;

    @Field("room_name")
    private String roomName;

    @Field("members")
    private List<Long> members;

    @Field("created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PersistenceConstructor
    public ChatRoom(String roomId, String roomType, List<Long> members, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.members = members;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
