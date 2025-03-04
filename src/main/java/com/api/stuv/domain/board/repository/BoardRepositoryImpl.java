package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.dto.BoardDetailDTO;
import com.api.stuv.domain.board.dto.response.BoardResponse;
import com.api.stuv.domain.board.entity.QBoard;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.api.stuv.global.response.PageResponse;
import com.api.stuv.global.util.common.TemplateUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final S3ImageService s3ImageService;
    private final QBoard b = QBoard.board;
    private final QUser u = QUser.user;
    private final QUserCostume uc = QUserCostume.userCostume;
    private final QImageFile i = QImageFile.imageFile;

    @Override
    public PageResponse<BoardResponse> getBoardList(String title, Pageable pageable) {
        JPQLQuery<String> imageSubQuery = JPAExpressions
                .select(i.newFilename)
                .from(i)
                .where(i.entityId.eq(b.id).and(i.entityType.eq(EntityType.BOARD)))
                .groupBy(i.entityId)
                .orderBy(i.createdAt.asc())
                .limit(1);

        List<BoardResponse> query = jpaQueryFactory
                .select(
                        b.id,
                        b.title,
                        b.boardType,
                        b.context,
                        b.confirmedCommentId.isNotNull(),
                        TemplateUtils.timeFormater(b.createdAt),
                        imageSubQuery
                )
                .from(b)
                .where(b.title.contains(title))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(tuple -> new BoardResponse(
                        tuple.get(b.id),
                        tuple.get(b.title),
                        tuple.get(b.boardType),
                        tuple.get(b.context),
                        tuple.get(b.confirmedCommentId.isNotNull()),
                        tuple.get(TemplateUtils.timeFormater(b.createdAt)),
                        tuple.get(imageSubQuery) == null ? null : s3ImageService.generateImageFile(
                                EntityType.BOARD, tuple.get(b.id),tuple.get(imageSubQuery))
                )).toList();

        return PageResponse.of(new PageImpl<>(query, pageable, getTotalBoardListPage(title)));
    }

    @Override
    public BoardDetailDTO getBoardDetail(Long boardId) {
        return jpaQueryFactory
                .select(Projections.constructor(BoardDetailDTO.class,
                        b.title,
                        u.id,
                        u.nickname,
                        b.boardType,
                        b.createdAt,
                        b.confirmedCommentId.isNotNull(),
                        b.context,
                        uc.costumeId,
                        i.newFilename))
                .from(b).join(u).on(b.userId.eq(u.id))
                .leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(b.id.eq(boardId))
                .fetchOne();
    }

    @Override
    public List<String> getBoardDetailImage(Long boardId) {
        return jpaQueryFactory
                .select(i.newFilename)
                .from(i)
                .where(i.entityId.eq(boardId).and(i.entityType.eq(EntityType.BOARD)))
                .fetch();
    }

    private Long getTotalBoardListPage(String title) {
        return jpaQueryFactory.select(b.count()).from(b).where(b.title.contains(title)).fetchOne();
    }
}
