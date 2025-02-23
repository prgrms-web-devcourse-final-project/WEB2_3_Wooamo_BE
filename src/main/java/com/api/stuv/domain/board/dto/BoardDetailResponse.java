package com.api.stuv.domain.board.dto;

import java.util.List;

public record BoardDetailResponse(
        String title,
        Long userId,
        String nickname,
        String profile,
        String boardType,
        String createAt,
        Boolean isConfirm,
        String context,
        List<String> images
) {}
