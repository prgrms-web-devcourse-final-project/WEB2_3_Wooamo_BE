package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.dto.CommentDTO;
import com.api.stuv.domain.image.entity.EntityType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static com.api.stuv.domain.board.entity.QBoard.board;
import static com.api.stuv.domain.board.entity.QComment.comment;
import static com.api.stuv.domain.image.entity.QImageFile.imageFile;
import static com.api.stuv.domain.user.entity.QUser.user;
import static com.api.stuv.domain.user.entity.QUserCostume.userCostume;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CommentDTO> getCommentList(Long boardId) {
        return jpaQueryFactory
                .select(Projections.constructor(CommentDTO.class,
                        comment.id,
                        comment.userId,
                        user.nickname,
                        comment.context,
                        comment.createdAt,
                        board.confirmedCommentId,
                        userCostume.costumeId,
                        imageFile.newFilename))
                .from(comment).join(board).on(comment.boardId.eq(board.id))
                .join(user).on(comment.userId.eq(user.id))
                .leftJoin(userCostume).on(user.costumeId.eq(userCostume.id))
                .leftJoin(imageFile).on(userCostume.costumeId.eq(imageFile.entityId).and(imageFile.entityType.eq(EntityType.COSTUME)))
                .where(comment.boardId.eq(boardId))
                .orderBy(comment.createdAt.desc()).fetch();
    }
}