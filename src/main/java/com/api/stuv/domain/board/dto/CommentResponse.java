package com.api.stuv.domain.board.dto;

public record CommentResponse(
        Long commentId,
        Long userId,
        String nickname,
        String profile,
        String context,
        String createdAt,
        boolean isConfirm
) {}
