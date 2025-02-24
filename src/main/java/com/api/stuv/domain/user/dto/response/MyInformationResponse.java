package com.api.stuv.domain.user.dto.response;

import java.math.BigDecimal;

public record MyInformationResponse(
        Long id,
        String context,
        String blogLink,
        String nickname,
        BigDecimal point
) {
}
