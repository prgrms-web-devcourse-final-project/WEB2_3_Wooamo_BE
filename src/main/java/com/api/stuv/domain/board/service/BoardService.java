package com.api.stuv.domain.board.service;

import com.api.stuv.domain.board.dto.BoardResponse;
import com.api.stuv.domain.board.dto.CommentResponse;
import com.api.stuv.domain.board.repository.BoardRepository;
import com.api.stuv.domain.board.repository.CommentRepository;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.response.PageResponse;
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

    // TODO : 이후 이미지 다운로드 기능 추가해 주세요!
    public PageResponse<BoardResponse> getBoardList(String title, Pageable pageable) {
        return boardRepository.getBoardList(title, pageable, "http://localhost:8080/api/v1/board/image/");
    }

    // TODO : 이후 이미지 다운로드 기능 추가해 주세요!
    public PageResponse<CommentResponse> getCommentList(Long boardId, Pageable pageable) {
        if (!boardRepository.existsById(boardId)) throw new NotFoundException(ErrorCode.BOARD_NOT_FOUND);
        return commentRepository.getCommentList(boardId, pageable, "http://localhost:8080/api/v1/costume/");
    }
}
