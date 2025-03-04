package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.dto.CommentDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {
    List<CommentDTO> getCommentList(Long boardId, Pageable pageable);
    Long getCommentCount(Long boardId);
}