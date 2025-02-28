package com.api.stuv.domain.timer.repository;

import com.api.stuv.domain.timer.entity.QStudyTime;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

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
}
