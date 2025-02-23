package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.BoardResponse;
import com.api.stuv.domain.board.entity.QBoard;
import com.api.stuv.global.response.PageResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import com.api.stuv.global.util.email.common.TemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QBoard b = QBoard.board;

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

    private Long getTotalBoardListPage(String title) {
        return jpaQueryFactory.select(b.count()).from(b).where(b.title.contains(title)).fetchOne();
    }
}
