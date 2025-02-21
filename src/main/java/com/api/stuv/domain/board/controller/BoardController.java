package com.api.stuv.domain.board.controller;

import com.api.stuv.domain.board.dto.BoardResponse;
import com.api.stuv.domain.board.dto.CommentResponse;
import com.api.stuv.domain.board.service.BoardService;
import com.api.stuv.global.response.ApiResponse;
import com.api.stuv.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Tag(name = "Board", description = "게시판 관련 API")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시판 목록 조회 API", description = "게시판 목록을 조회 합니다.")
    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResponse<BoardResponse>>> getBoardList(
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(boardService.getBoardList(title, PageRequest.of(page, size))));
    }

    @Operation(summary = "코멘트 목록 조회 API", description = "코멘트 목록을 조회 합니다.")
    @GetMapping("/{boardId}/comment")
    public ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> getCommentList(
            @PathVariable Long boardId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(boardService.getCommentList(boardId, PageRequest.of(page, size))));
    }
}
