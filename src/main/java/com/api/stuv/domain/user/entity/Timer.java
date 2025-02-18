package com.api.stuv.domain.user.entity;

import com.api.stuv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "timers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Timer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Builder
    public Timer(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }
}