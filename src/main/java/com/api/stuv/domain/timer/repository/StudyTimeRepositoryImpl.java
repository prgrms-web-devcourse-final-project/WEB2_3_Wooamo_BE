package com.api.stuv.domain.timer.repository;

import com.api.stuv.domain.timer.entity.QStudyTime;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StudyTimeRepositoryImpl implements StudyTimeRepositoryCustom {
    private final JPAQueryFactory factory;
    private final QStudyTime st = QStudyTime.studyTime1;

    @Override
    public Long findStudyTimeById(Long userId, Long categoryId, LocalDate date) {
        return factory.select(st.studyTime.coalesce(0L))
                .from(st)
                .where(
                        st.userId.eq(userId),
                        st.categoryId.eq(categoryId),
                        st.studyDate.eq(date)
                )
                .fetchOne();
    }

    @Override
    public Map<LocalDate, Long> sumTotalStudyTimeByDate(Long userId, LocalDate startDate, LocalDate endDate) {
        return factory.select(
                        st.studyDate,
                        st.studyTime.sum().coalesce(0L)
                )
                .from(st)
                .where(st.userId.eq(userId)
                        .and(st.studyDate.between(startDate, endDate)))
                .groupBy(st.studyDate)
                .orderBy(st.studyDate.asc())
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(st.studyDate),
                        tuple -> tuple.get(1, Long.class)
                ));
    }

    @Override
    public Long sumTotalStudyTimeByWeekly(Long userId, LocalDate startOfWeek, LocalDate endOfWeek) {
        return factory.select(
                        st.studyTime.sum().coalesce(0L)
                )
                .from(st)
                .where(st.userId.eq(userId)
                        .and(st.studyDate.between(startOfWeek, endOfWeek)))
                .fetchOne();
    }

    @Override
    public Long sumTotalStudyTimeByDaily(Long userId, LocalDate today) {
        return factory.select(
                        st.studyTime.sum().coalesce(0L)
                )
                .from(st)
                .where(st.userId.eq(userId)
                        .and(st.studyDate.eq(today)))
                .fetchOne();
    }
}
