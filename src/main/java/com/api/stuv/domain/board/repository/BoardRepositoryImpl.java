package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.BoardResponse;
import com.api.stuv.domain.board.entity.QBoard;
import com.api.stuv.domain.board.entity.QComment;
import com.api.stuv.global.response.PageResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QBoard b = QBoard.board;
    private final QComment c = QComment.comment;

    public PageResponse<BoardResponse> getBoardList(String title, Pageable pageable, String imageUrl) {
        BooleanExpression isConfirm = JPAExpressions.selectOne().from(c)
                .where(c.boardId.eq(b.id).and(c.isConfirm.eq(true))).exists();

        JPAQuery<BoardResponse> query = jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class, b.id.as("boardId"), b.title, b.boardType, isConfirm, timeFormater(b.createdAt), getImageUrl(imageUrl, b.id).as("image")))
                .from(b)
                .where(b.title.contains(title));

        return applyPage(query, pageable, getBoardCount(title));
    }

    public <T> PageResponse<T> applyPage(JPAQuery<T> query, Pageable pageable, Long count) {
        List<T> content = query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        return PageResponse.of(new PageImpl<>(content, pageable, count));
    }

    public Long getBoardCount(String title) {
        return jpaQueryFactory.select(b.count()).from(b).where(b.title.contains(title)).fetchOne();
    }

    public StringTemplate timeFormater(DateTimePath<LocalDateTime> dateTimePath) {
        return Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d %H:%i:%s')", dateTimePath);
    }

    public StringTemplate getImageUrl(String imageUrl, NumberPath<Long> id) {
        return Expressions.stringTemplate("CONCAT({0}, {1})", imageUrl, id);
    }
}
