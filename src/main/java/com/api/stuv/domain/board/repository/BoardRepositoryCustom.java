package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.dto.BoardDetailDTO;
import com.api.stuv.domain.board.dto.dto.BoardListDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardRepositoryCustom {
    List<BoardListDTO> getBoardList(String title, Pageable pageable);
    BoardDetailDTO getBoardDetail(Long boardId);
    List<String> getBoardDetailImage(Long boardId);
    Long getTotalBoardListPage(String title);
}