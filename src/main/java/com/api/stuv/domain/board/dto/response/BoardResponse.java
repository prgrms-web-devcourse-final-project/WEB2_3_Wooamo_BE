package com.api.stuv.domain.board.dto.response;

public record BoardResponse (
        Long boardId,
        String title,
        String boardType,
        String context,
        boolean isConfirm,
        String createdAt,
        String image
) {}

