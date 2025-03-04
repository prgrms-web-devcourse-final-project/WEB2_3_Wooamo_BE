package com.api.stuv.domain.board.dto.dto;

import com.api.stuv.domain.board.entity.BoardType;

import java.time.LocalDateTime;

public record BoardDetailDTO(
        String title,
        Long userId,
        String nickname,
        BoardType boardType,
        LocalDateTime createdAt,
        Boolean isConfirm,
        String context,
        Long costumeId,
        String newFilename
) {}
