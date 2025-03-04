package com.api.stuv.domain.board.dto.dto;

import com.api.stuv.domain.board.entity.BoardType;

import java.time.LocalDateTime;

public record BoardListDTO(
        Long boardId,
        String title,
        BoardType boardType,
        String context,
        boolean isConfirm,
        LocalDateTime createdAt,
        String newFilename
) {}
