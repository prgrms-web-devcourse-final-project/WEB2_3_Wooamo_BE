package com.api.stuv.domain.user.dto;

import com.api.stuv.domain.board.entity.BoardType;

public record UserBoardListDTO(
        Long boardId,
        String title,
        String context,
        String boardType,
        String createdAt,
        String newFileName
) {
    public UserBoardListDTO(
            Long boardId,
            String title,
            String context,
            BoardType boardType,
            String createdAt,
            String newFileName
    ) {
        this(boardId, title, context, boardType.toString(), createdAt, newFileName);
    }
}