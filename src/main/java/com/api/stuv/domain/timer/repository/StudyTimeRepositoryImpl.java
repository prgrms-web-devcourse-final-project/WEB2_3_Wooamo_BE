package com.api.stuv.domain.timer.repository;

import com.api.stuv.domain.timer.dto.UserRankDTO;
import com.api.stuv.domain.timer.dto.response.StudyDateTimeResponse;
import com.api.stuv.domain.timer.entity.QStudyTime;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.api.stuv.domain.timer.util.TimerUtil.formatSecondsToTime;

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
    public StudyDateTimeResponse sumTotalStudyTimeByDaily(Long userId, LocalDate today) {
        Tuple result = factory.select(
                        st.studyDate.coalesce(today),
                        st.studyTime.sum().coalesce(0L)
                )
                .from(st)
                .where(st.userId.eq(userId)
                        .and(st.studyDate.eq(today)))
                .fetchOne();

        if (result == null) {
            return new StudyDateTimeResponse(today, formatSecondsToTime(0L));
        }

        LocalDate studyDate = result.get(st.studyDate.coalesce(today));
        Long studyTime = result.get(st.studyTime.sum().coalesce(0L));

        return new StudyDateTimeResponse(studyDate, formatSecondsToTime(studyTime));
    }

    @Override
    public List<UserRankDTO> findWeeklyUserRank(LocalDate startOfWeek, LocalDate endOfWeek) {
        return factory.select(Projections.constructor(
                        UserRankDTO.class,
                        st.userId,
                        st.studyTime.sum().coalesce(0L)
                ))
                .from(st)
                .where(st.studyDate.between(startOfWeek, endOfWeek))
                .groupBy(st.userId)
                .orderBy(st.studyTime.sum().desc())
                .fetch();
    }
}
