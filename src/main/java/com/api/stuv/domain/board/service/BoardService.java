package com.api.stuv.domain.board.service;

import com.api.stuv.domain.board.dto.BoardResponse;
import com.api.stuv.domain.board.entity.QBoard;
import com.api.stuv.domain.board.entity.QComment;
import com.api.stuv.domain.board.repository.BoardRepository;
import com.api.stuv.domain.board.repository.CommentRepository;
import com.api.stuv.global.exception.BadRequestException;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.response.PageResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final JPAQueryFactory jpaQueryFactory;

    // TODO : 이후 이미지 다운로드 기능 추가해 주세요!
    public PageResponse<BoardResponse> getBoardList(String title, Pageable pageable) {
        QBoard b = QBoard.board;
        QComment c = QComment.comment;

        if ( pageable.getPageSize() < 1 ) throw new BadRequestException(ErrorCode.INVALID_PAGE_SIZE);

        BooleanExpression isConfirm = JPAExpressions.selectOne().from(c)
                .where(c.boardId.eq(b.id).and(c.isConfirm.eq(true))).exists();

        JPAQuery<BoardResponse> query = jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class,
                        b.id.as("boardId"), b.title, b.boardType, isConfirm,
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d %H:%i:%s')", b.createdAt),
                        Expressions.stringTemplate("CONCAT('http://example.image.text/board/', {0})", b.id).as("imageUrl")))
                .from(b)
                .where(b.title.contains(title));

        List<BoardResponse> content = query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        Long pageCount = jpaQueryFactory.select(b.count()).from(b).where(b.title.contains(title)).fetchOne();

        log.error("pageCount: " + pageCount + ", pageable.getPageNumber(): " + pageable.getPageNumber());

        if ( pageCount == null || pageCount < pageable.getPageNumber() ) throw new BadRequestException(ErrorCode.INVALID_PAGE_NUMBER);

        return PageResponse.of(new PageImpl<>(content, pageable, pageCount));
    }
}
