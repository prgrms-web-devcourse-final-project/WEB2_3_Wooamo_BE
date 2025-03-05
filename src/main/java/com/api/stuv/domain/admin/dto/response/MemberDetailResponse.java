package com.api.stuv.domain.admin.dto.response;

public record MemberDetailResponse(
        Long memberId,
        String profile,
        String nickname,
        String isAuth
) {}
