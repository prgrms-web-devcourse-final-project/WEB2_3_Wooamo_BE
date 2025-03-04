package com.api.stuv.domain.board.dto.response;

import java.util.List;

public record BoardDetailResponse(
        String title,
        Long userId,
        String nickname,
        String boardType,
        String createdAt,
        Boolean isConfirm,
        String context,
        String profile,
        List<String> images
) {}
