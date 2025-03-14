package com.api.stuv.domain.user.dto.response;

import com.api.stuv.domain.user.entity.RoleType;

import java.math.BigDecimal;

public record MyInformationResponse(
        Long userId,
        String context,
        String link,
        String nickname,
        BigDecimal point,
        RoleType role,
        String profile,
        Long friends
) {
}
