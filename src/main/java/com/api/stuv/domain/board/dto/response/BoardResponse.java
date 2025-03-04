package com.api.stuv.domain.board.dto.response;

import com.api.stuv.domain.board.entity.BoardType;

public record BoardResponse (
        Long boardId,
        String title,
        String boardType,
        String context,
        boolean isConfirm,
        String createdAt,
        String image
) {
    public BoardResponse(
            Long boardId,
            String title,
            BoardType boardType,
            String context,
            boolean confirmedCommentId,
            String createdAt,
            String image
    ) {
        this(boardId, title, boardType.toString(), context, confirmedCommentId, createdAt, image);
    }
}

