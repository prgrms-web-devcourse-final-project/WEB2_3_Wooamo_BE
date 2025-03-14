package com.api.stuv.domain.board.dto.request;

import java.util.List;

public record BoardUpdateRequest(
        String title,
        String context,
        List<String> deletedImages
) {}
