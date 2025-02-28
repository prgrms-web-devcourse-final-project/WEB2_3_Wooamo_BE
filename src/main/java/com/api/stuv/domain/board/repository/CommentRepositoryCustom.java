package com.api.stuv.domain.board.repository;

import com.api.stuv.domain.board.dto.CommentResponse;
import com.api.stuv.global.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
    PageResponse<CommentResponse> getCommentList(Long boardId, Pageable pageable);
}
