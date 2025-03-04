package com.api.stuv.domain.board.dto.dto;

import java.time.LocalDateTime;

public record CommentDTO(
        Long commentId,
        Long userId,
        String nickname,
        String context,
        LocalDateTime createdAt,
        Long isConfirm,
        Long costumeId,
        String newFilename
) {}
