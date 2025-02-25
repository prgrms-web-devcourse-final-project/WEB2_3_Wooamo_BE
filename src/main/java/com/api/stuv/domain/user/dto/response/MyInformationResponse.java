package com.api.stuv.domain.user.dto.response;

import java.math.BigDecimal;

public record MyInformationResponse(
        Long userId,
        String context,
        String link,
        String nickname,
        BigDecimal point,
        String profile
) {
}
