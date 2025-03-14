package com.api.stuv.domain.admin.dto.response;

import java.math.BigDecimal;

public record PointSalesResponse(
        String createdAt,
        String nickname,
        BigDecimal amount,
        BigDecimal point
) {
}
