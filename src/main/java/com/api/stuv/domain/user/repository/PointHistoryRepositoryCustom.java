package com.api.stuv.domain.user.repository;

public interface PointHistoryRepositoryCustom {
    boolean existsHistoryByUserIdBetweenWeekends(Long userId);
}
