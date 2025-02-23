package com.api.stuv.domain.board.dto;

import com.api.stuv.domain.board.entity.Board;
import com.api.stuv.domain.board.entity.BoardType;

public record BoardRequest(
        String title,
        String context,
        String boardType
) {
    public record createBoard(
            Long userId,
            Long confirmedCommentId,
            String title,
            String context,
            BoardType boardType
    ) {
        public static Board from(Long userId, BoardRequest boardRequest) {
            return new Board(userId, boardRequest.title(), boardRequest.context(), BoardType.valueOf(boardRequest.boardType()));
        }
    }
}
