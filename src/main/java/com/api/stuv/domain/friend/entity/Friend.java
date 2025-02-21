package com.api.stuv.domain.friend.entity;

import com.api.stuv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "friends")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long friendId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;

    public Friend(Long id, Long userId, Long friendId, FriendStatus status) {
        this.id = id;
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }

    public static Friend init(Long userId, Long friendId) {
        return new Friend(null, userId, friendId, FriendStatus.PENDING);
    }

    public void accept() { this.status = FriendStatus.ACCEPTED; }
}
