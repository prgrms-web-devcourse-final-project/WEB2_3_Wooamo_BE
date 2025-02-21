package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.CommentResponse;
import com.api.stuv.domain.board.entity.QBoard;
import com.api.stuv.domain.board.entity.QComment;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.global.response.PageResponse;
import com.api.stuv.global.util.email.common.TemplateUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

@Slf4j
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QBoard b = QBoard.board;
    private final QComment c = QComment.comment;
    private final QUser u = QUser.user;

    @Override
    public PageResponse<CommentResponse> getCommentList(Long boardId, Pageable pageable, String imageUrl) {
        Long confirmedCommentId = jpaQueryFactory.select(b.confirmedCommentId).from(b).where(b.id.eq(boardId)).fetchOne();
        JPAQuery<CommentResponse> query = jpaQueryFactory
                .select(Projections.constructor(CommentResponse.class,
                        c.id.as("commentId"),
                        c.userId,
                        u.nickname,
                        TemplateUtils.getImageUrl(imageUrl, u.costumeId).as("profile"),
                        c.context,
                        TemplateUtils.timeFormater(c.createdAt).as("createdAt"),
                        c.id.eq(confirmedCommentId == null ? -1L : confirmedCommentId ).as("isConfirm")))
                .from(c).leftJoin(u).on(c.userId.eq(u.id))
                .where(c.boardId.eq(boardId));
        return PageResponse.applyPage(query, pageable, getCommentCount(boardId));
    }

    private Long getCommentCount(Long boardId) {
        return jpaQueryFactory.select(c.count()).from(c).where(c.boardId.eq(boardId)).fetchOne();
    }
}
