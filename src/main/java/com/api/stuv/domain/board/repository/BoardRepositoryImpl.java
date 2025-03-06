package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.dto.BoardDetailDTO;
import com.api.stuv.domain.board.dto.dto.BoardListDTO;
import com.api.stuv.domain.image.entity.EntityType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import static com.api.stuv.domain.board.entity.QBoard.board;
import static com.api.stuv.domain.user.entity.QUser.user;
import static com.api.stuv.domain.user.entity.QUserCostume.userCostume;
import static com.api.stuv.domain.image.entity.QImageFile.imageFile;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BoardListDTO> getBoardList(String title, Pageable pageable) {
        JPQLQuery<LocalDateTime> imageSubQuery = jpaQueryFactory
                .select(imageFile.createdAt.min())
                .from(imageFile)
                .where(imageFile.entityId.eq(board.id).and(imageFile.entityType.eq(EntityType.BOARD)));
        return jpaQueryFactory
                .select(Projections.constructor(BoardListDTO.class,
                        board.id,
                        board.title,
                        board.boardType,
                        board.context,
                        board.confirmedCommentId.isNotNull(),
                        board.createdAt,
                        imageFile.newFilename
                ))
                .from(board).leftJoin(imageFile).on(board.id.eq(imageFile.entityId).and(imageFile.entityType.eq(EntityType.BOARD)))
                .where(board.title.contains(title))
                .where(imageFile.createdAt.isNull().or(imageFile.createdAt.eq(imageSubQuery)))
                .orderBy(board.createdAt.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
    }

    @Override
    public Long getTotalBoardListPage(String title) {
        return jpaQueryFactory.select(board.count()).from(board).where(board.title.contains(title)).fetchOne();
    }

    @Override
    public BoardDetailDTO getBoardDetail(Long boardId) {
        return jpaQueryFactory
                .select(Projections.constructor(BoardDetailDTO.class,
                        board.title,
                        user.id,
                        user.nickname,
                        board.boardType,
                        board.createdAt,
                        board.confirmedCommentId.isNotNull(),
                        board.context,
                        userCostume.costumeId,
                        imageFile.newFilename))
                .from(board).join(user).on(board.userId.eq(user.id))
                .leftJoin(userCostume).on(user.costumeId.eq(userCostume.id))
                .leftJoin(imageFile).on(userCostume.costumeId.eq(imageFile.entityId).and(imageFile.entityType.eq(EntityType.COSTUME)))
                .where(board.id.eq(boardId))
                .fetchOne();
    }

    @Override
    public List<String> getBoardDetailImage(Long boardId) {
        return jpaQueryFactory
                .select(imageFile.newFilename)
                .from(imageFile)
                .where(imageFile.entityId.eq(boardId).and(imageFile.entityType.eq(EntityType.BOARD)))
                .fetch();
    }
}
