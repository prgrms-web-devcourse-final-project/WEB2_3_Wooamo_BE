package com.api.stuv.domain.user.dto.response;

import com.api.stuv.domain.board.entity.BoardType;

public record UserBoardListResponse(
        Long boardId,
        String title,
        String context,
        String boardType,
        String createAt,
        String image
) {
    public UserBoardListResponse(
            Long boardId,
            String title,
            String context,
            BoardType boardType,
            String createdAt,
            String image
    ) {
        this(boardId, title, context, boardType.toString(), createdAt, image);
    }
}
