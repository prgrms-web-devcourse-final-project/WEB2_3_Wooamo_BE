package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.BoardDetailResponse;
import com.api.stuv.domain.board.dto.BoardResponse;
import com.api.stuv.domain.board.entity.QBoard;
import com.api.stuv.domain.image.entity.ImageType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.response.PageResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import com.api.stuv.global.util.email.common.TemplateUtils;

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
    public PageResponse<BoardResponse> getBoardList(String title, Pageable pageable, String imageUrl) {
        JPAQuery<BoardResponse> query = jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class,
                        b.id.as("boardId"),
                        b.title,
                        b.boardType,
                        b.confirmedCommentId.isNotNull(),
                        TemplateUtils.timeFormater(b.createdAt),
                        TemplateUtils.getImageUrl(imageUrl, b.id).as("image")))
                .from(b)
                .where(b.title.contains(title));

        return PageResponse.applyPage(query, pageable, getTotalBoardListPage(title));
    }

    @Override
    public BoardDetailResponse getBoardDetail(Long boardId) {
        Tuple boardDetails = jpaQueryFactory
                .select(b.title,
                        u.id,
                        u.nickname,
                        u.costumeId,
                        b.boardType,
                        TemplateUtils.timeFormater(b.createdAt),
                        b.confirmedCommentId.isNotNull(),
                        b.context)
                .from(b).leftJoin(u).on(b.userId.eq(u.id))
                .where(b.id.eq(boardId))
                .fetchOne();

        if (Objects.isNull(boardDetails)) throw new NotFoundException(ErrorCode.BOARD_NOT_FOUND);

        Tuple costumeDetails = jpaQueryFactory
                .select(i.id, i.newFilename)
                .from(uc).leftJoin(i).on(uc.costumeId.eq(i.id))
                .where(uc.id.eq(boardDetails.get(u.costumeId)))
                .fetchOne();

        return new BoardDetailResponse(
                boardDetails.get(b.title),
                boardDetails.get(u.id),
                boardDetails.get(u.nickname),
                ( costumeDetails == null ) ? null : s3ImageService.generateImageFile(ImageType.COSTUME, costumeDetails.get(i.id), costumeDetails.get(i.newFilename)),
                Objects.requireNonNull(boardDetails.get(b.boardType)).toString(),
                boardDetails.get(TemplateUtils.timeFormater(b.createdAt)),
                boardDetails.get(b.confirmedCommentId.isNotNull()),
                boardDetails.get(b.context),
                null
        );
    }

    private Long getTotalBoardListPage(String title) {
        return jpaQueryFactory.select(b.count()).from(b).where(b.title.contains(title)).fetchOne();
    }
}
