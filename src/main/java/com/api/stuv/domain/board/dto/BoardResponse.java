package com.api.stuv.domain.board.dto;

import com.api.stuv.domain.board.entity.BoardType;

public record BoardResponse (
        Long boardId,
        String title,
        BoardType boardType,
        boolean isConfirm,
        String createdAt,
        String imageUrl
) {}

