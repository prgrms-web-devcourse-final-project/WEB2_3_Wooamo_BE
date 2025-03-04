package com.api.stuv.domain.board.dto.response;

public record CommentResponse(
        Long commentId,
        Long userId,
        String nickname,
        String context,
        String createdAt,
        boolean isConfirm,
        String profile
) {}