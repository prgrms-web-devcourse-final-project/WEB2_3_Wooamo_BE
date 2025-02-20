package com.api.stuv.domain.board.dto;

import com.api.stuv.domain.board.entity.BoardType;

public record BoardResponse (
        Long boardId,
        String title,
        String boardType,
        boolean isConfirm,
        String createdAt,
        String image
) {
    public BoardResponse(
            Long boardId,
            String title,
            BoardType boardType,
            boolean confirmedCommentId,
            String createdAt,
            String image
    ) {
        this(boardId, title, boardType.toString(), confirmedCommentId, createdAt, image);
    }
}

