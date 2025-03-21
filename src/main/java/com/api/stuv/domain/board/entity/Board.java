package com.api.stuv.domain.board.entity;

import com.api.stuv.domain.board.dto.request.BoardUpdateRequest;
import com.api.stuv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "boards")
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long confirmedCommentId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType;

    public Board(Long userId, String title, String context, BoardType boardType) {
        this.userId = userId;
        this.title = title;
        this.context = context;
        this.boardType = boardType;
    }

    public void confirmComment(Long commentId) {
        this.confirmedCommentId = commentId;
    }

    public void update(BoardUpdateRequest request) {
        this.title = request.title();
        this.context = request.context();
    }
}