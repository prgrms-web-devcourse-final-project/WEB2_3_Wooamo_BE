package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.BoardDetailResponse;
import com.api.stuv.domain.board.dto.BoardResponse;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
    PageResponse<BoardResponse> getBoardList(String title, Pageable pageable, String imageUrl);
    BoardDetailResponse getBoardDetail(Long boardId);
}
