package com.api.stuv.domain.socket.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.util.ArrayList;
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

    @Field("max_members")
    private Integer maxMembers;

    @Field("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PersistenceConstructor
    public ChatRoom(String roomId, String roomType, List<Long> members, Integer maxMembers, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.members = members != null ? members : new ArrayList<>();
        this.maxMembers = maxMembers;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
