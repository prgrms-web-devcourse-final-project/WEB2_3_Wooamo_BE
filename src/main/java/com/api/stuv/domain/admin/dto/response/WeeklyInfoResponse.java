package com.api.stuv.domain.admin.dto.response;

import java.math.BigDecimal;

public record WeeklyInfoResponse(
        Long weeklySignupUser,
        String image,
        BigDecimal weeklyPointSales
) {
}
