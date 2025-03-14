package com.api.stuv.domain.board.entity;

import com.api.stuv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comments")
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long boardId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String context;

    @Column(nullable = false)
    private Long userId;

    public Comment(Long boardId, String context, Long userId) {
        this.boardId = boardId;
        this.context = context;
        this.userId = userId;
    }

    public static Comment create(Long boardId, Long userId, String context) {
        return new Comment(boardId, context, userId);
    }
}