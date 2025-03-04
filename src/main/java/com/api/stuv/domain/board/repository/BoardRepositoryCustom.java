package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.dto.BoardDetailDTO;
import com.api.stuv.domain.board.dto.response.BoardResponse;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardRepositoryCustom {
    PageResponse<BoardResponse> getBoardList(String title, Pageable pageable);
    BoardDetailDTO getBoardDetail(Long boardId);
    List<String> getBoardDetailImage(Long boardId);
}
