package com.api.stuv.domain.user.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PointHistoryRepositoryCustom {
    boolean existsHistoryByUserIdBetweenWeekends(Long userId);
    BigDecimal sumWeekendSalesPoint(LocalDateTime startDate, LocalDateTime endDate);
}
