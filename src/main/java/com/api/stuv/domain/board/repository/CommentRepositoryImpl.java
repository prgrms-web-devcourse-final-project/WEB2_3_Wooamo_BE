package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.CommentResponse;
import com.api.stuv.domain.board.entity.QBoard;
import com.api.stuv.domain.board.entity.QComment;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.QImageFile;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.user.entity.QUser;
import com.api.stuv.domain.user.entity.QUserCostume;
import com.api.stuv.global.response.PageResponse;
import com.api.stuv.global.util.email.common.TemplateUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final S3ImageService s3ImageService;
    private final QBoard b = QBoard.board;
    private final QComment c = QComment.comment;
    private final QUser u = QUser.user;
    private final QUserCostume uc = QUserCostume.userCostume;
    private final QImageFile i = QImageFile.imageFile;

    @Override
    public PageResponse<CommentResponse> getCommentList(Long boardId, Pageable pageable, String imageUrl) {
        Long confirmedCommentId = jpaQueryFactory.select(b.confirmedCommentId).from(b).where(b.id.eq(boardId)).fetchOne();
        List<CommentResponse> response = jpaQueryFactory
                .select(c.id,
                        c.userId,
                        u.nickname,
                        uc.costumeId,
                        i.newFilename,
                        c.context,
                        TemplateUtils.timeFormater(c.createdAt),
                        c.id.eq(confirmedCommentId == null ? -1L : confirmedCommentId ))
                .from(c).join(u).on(c.userId.eq(u.id))
                .leftJoin(uc).on(u.costumeId.eq(uc.id))
                .leftJoin(i).on(uc.costumeId.eq(i.entityId).and(i.entityType.eq(EntityType.COSTUME)))
                .where(c.boardId.eq(boardId))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch()
                .stream().map( tuple -> new CommentResponse(
                        tuple.get(c.id),
                        tuple.get(c.userId),
                        tuple.get(u.nickname),
                        tuple.get(i.newFilename) == null ? null : s3ImageService.generateImageFile(EntityType.COSTUME, tuple.get(uc.costumeId), tuple.get(i.newFilename)),
                        tuple.get(c.context),
                        tuple.get(6, String.class),
                        tuple.get(7, Boolean.class)
                        )
                ).toList();
        return PageResponse.of(new PageImpl<>(response, pageable, getCommentCount(boardId)));
    }

    private Long getCommentCount(Long boardId) {
        return jpaQueryFactory.select(c.count()).from(c).where(c.boardId.eq(boardId)).fetchOne();
    }
}
