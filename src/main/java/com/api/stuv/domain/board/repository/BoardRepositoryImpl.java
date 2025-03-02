package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.BoardDetailResponse;
import com.api.stuv.domain.board.dto.BoardResponse;
import com.api.stuv.domain.board.entity.QBoard;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.response.PageResponse;
import com.api.stuv.global.util.common.TemplateUtils;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;


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
    public BoardDetailResponse getBoardDetail(Long boardId) {
        Tuple boardDetails = jpaQueryFactory
                .select(b.title,
                        u.id,
                        u.nickname,
                        uc.costumeId,
                        i.newFilename,
                        b.boardType,
                        TemplateUtils.timeFormater(b.createdAt),
                        b.confirmedCommentId.isNotNull(),
                        b.context)
                .from(b).join(u).on(b.userId.eq(u.id))
                .leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(b.id.eq(boardId))
                .fetchOne();
        if (Objects.isNull(boardDetails)) throw new NotFoundException(ErrorCode.BOARD_NOT_FOUND);

        List<String> imageList = jpaQueryFactory
                .select(i.newFilename)
                .from(i)
                .leftJoin(b).on(b.id.eq(i.entityId))
                .where(i.entityType.eq(EntityType.BOARD).and(b.id.eq(boardId)))
                .fetch()
                .stream()
                .map( filename -> filename == null ? null : s3ImageService.generateImageFile(EntityType.BOARD, boardId, filename)).toList();

        return new BoardDetailResponse(
                boardDetails.get(b.title),
                boardDetails.get(u.id),
                boardDetails.get(u.nickname),
                boardDetails.get(i.newFilename) == null ? null :s3ImageService.generateImageFile(EntityType.COSTUME, boardDetails.get(uc.costumeId), boardDetails.get(i.newFilename)),
                Objects.requireNonNull(boardDetails.get(b.boardType)).toString(),
                boardDetails.get(TemplateUtils.timeFormater(b.createdAt)),
                boardDetails.get(b.confirmedCommentId.isNotNull()),
                boardDetails.get(b.context),
                imageList
        );
    }

    private Long getTotalBoardListPage(String title) {
        return jpaQueryFactory.select(b.count()).from(b).where(b.title.contains(title)).fetchOne();
    }
}
