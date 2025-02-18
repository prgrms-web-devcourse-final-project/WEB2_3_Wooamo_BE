package com.api.stuv.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "user_costumes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCostume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long costumeId;

    @Builder
    public UserCostume(Long userId, Long costumeId) {
        this.userId = userId;
        this.costumeId = costumeId;
    }
}
