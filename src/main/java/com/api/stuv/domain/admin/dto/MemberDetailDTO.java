package com.api.stuv.domain.admin.dto;

public record MemberDetailDTO(
        Long memberId,
        String profile,
        String nickname,
        String isAuth
) {}
