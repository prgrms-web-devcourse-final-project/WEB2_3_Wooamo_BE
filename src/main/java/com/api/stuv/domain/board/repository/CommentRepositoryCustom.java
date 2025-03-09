package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.dto.CommentDTO;

import java.util.List;

public interface CommentRepositoryCustom {
    List<CommentDTO> getCommentList(Long boardId);
}