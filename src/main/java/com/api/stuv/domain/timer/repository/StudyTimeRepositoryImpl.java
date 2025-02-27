package com.api.stuv.domain.timer.repository;

import com.api.stuv.domain.timer.dto.response.TimerListResponse;
import com.api.stuv.domain.timer.entity.QStudyTime;
import com.api.stuv.domain.timer.entity.QTimer;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class StudyTimeRepositoryImpl implements StudyTimeRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QUser u = QUser.user;
    private final QTimer t = QTimer.timer;
    private final QStudyTime st = QStudyTime.studyTime1;

    @Override
    public List<TimerListResponse> findAllStudyTimeByUserId(Long userId) {
        List<Tuple> resultList = jpaQueryFactory
                .select(st.id, st.categoryId, t.name, st.studyDate, st.studyTime)
                .from(st)
                .join(t)
                .on(st.categoryId.eq(t.id))
                .where(st.userId.eq(userId))
                .fetch();

        if(resultList.isEmpty()){
            throw new NotFoundException(ErrorCode.TIMER_NOT_EXIST);
        }

        List<TimerListResponse> query = resultList.stream()
                .map(tuple -> new TimerListResponse(
                        tuple.get(st.id),
                        tuple.get(st.categoryId),
                        tuple.get(t.name),
                        tuple.get(st.studyDate),
                        tuple.get(st.studyTime)
                ))
                .toList();

        return query;
    }
}
