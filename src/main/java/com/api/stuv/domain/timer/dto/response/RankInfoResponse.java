package com.api.stuv.domain.timer.dto.response;

public record RankInfoResponse(
        Long userId,
        String profile,
        String nickname,
        String studyTime
) {
}
