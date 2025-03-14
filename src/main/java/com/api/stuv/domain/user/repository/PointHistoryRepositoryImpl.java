package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.entity.HistoryType;
import com.api.stuv.domain.user.entity.QPointHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepositoryCustom {

    private final JPAQueryFactory factory;
    private final QPointHistory ph = QPointHistory.pointHistory;

    @Override
    public boolean existsHistoryByUserIdBetweenWeekends(Long userId) {
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                .atTime(23, 59, 59);

        return factory.selectOne()
                .from(ph)
                .where(ph.userId.eq(userId)
                        .and(ph.createdAt.between(startOfWeek, endOfWeek))
                        .and(ph.transactionType.eq(HistoryType.RANKING)))
                .fetchFirst() != null;
    }

    @Override
    public BigDecimal sumWeekendSalesPoint(LocalDateTime startDate, LocalDateTime endDate) {
        return Optional.ofNullable(
                factory
                        .select(ph.amount.sum())
                        .from(ph)
                        .where(ph.createdAt.between(startDate, endDate)
                                .and(ph.transactionType.eq(HistoryType.CHARGE)))
                        .fetchOne()
        ).orElse(BigDecimal.ZERO);
    }
}
