package com.api.stuv.domain.board.dto.request;

import com.api.stuv.domain.board.entity.Board;
import com.api.stuv.domain.board.entity.BoardType;

public record BoardRequest(
        String title,
        String context,
        String boardType
) {
    public static Board from(Long userId, BoardRequest boardRequest) {
        return new Board(userId, boardRequest.title(), boardRequest.context(), BoardType.fromText(boardRequest.boardType()));
    }
}
