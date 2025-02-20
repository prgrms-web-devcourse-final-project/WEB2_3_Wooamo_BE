package com.api.stuv.domain.board.service;

import com.api.stuv.domain.board.dto.BoardResponse;
import com.api.stuv.domain.board.repository.BoardRepository;
import com.api.stuv.domain.board.repository.BoardRepositoryCustom;
import com.api.stuv.domain.board.repository.BoardRepositoryImpl;
import com.api.stuv.domain.board.repository.CommentRepository;
import com.api.stuv.global.response.PageResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final JPAQueryFactory jpaQueryFactory;

    // TODO : 이후 이미지 다운로드 기능 추가해 주세요!
    public PageResponse<BoardResponse> getBoardList(String title, Pageable pageable) {
        BoardRepositoryCustom boardRepositoryCustom = new BoardRepositoryImpl(jpaQueryFactory);
        return boardRepositoryCustom.getBoardList(title, pageable, "http://localhost:8080/api/v1/board/image/");
    }
}
