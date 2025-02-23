package com.api.stuv.domain.board.controller;

import com.api.stuv.domain.board.dto.BoardDetailResponse;
import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.board.dto.BoardRequest;
import com.api.stuv.domain.board.dto.BoardResponse;
import com.api.stuv.domain.board.dto.CommentResponse;
import com.api.stuv.domain.board.service.BoardService;
import com.api.stuv.global.response.ApiResponse;
import com.api.stuv.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Tag(name = "Board", description = "게시판 관련 API")
public class BoardController {

    private final BoardService boardService;
    private final TokenUtil tokenUtil;

    @Operation(summary = "게시판 목록 조회 API", description = "게시판 목록을 조회 합니다.")
    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResponse<BoardResponse>>> getBoardList(
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(boardService.getBoardList(title, PageRequest.of(page, size))));
    }

    @Operation(summary = "게시판 생성 API", description = "게시판을 생성합니다.")
    @PostMapping( value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Long>>> createBoard(
            @RequestPart(value = "contents") @Valid BoardRequest boardRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> files
    ) {
        return ResponseEntity.ok().body(ApiResponse.success(boardService.createBoard(tokenUtil.getUserId(), boardRequest, files)));
    }

    @Operation(summary = "게시판 상세 조회 API", description = "게시판 상세를 조회 합니다.")
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardDetailResponse>> getBoardDetail(
            @PathVariable Long boardId
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(boardService.getBoardDetail(boardId)));
    }

    //TODO: 이후 유저 검증 로직 추가
    @Operation(summary = "코멘트 목록 조회 API", description = "코멘트 목록을 조회 합니다.")
    @GetMapping("/{boardId}/comment")
    public ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> getCommentList(
            @PathVariable Long boardId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(boardService.getCommentList(boardId, PageRequest.of(page, size))));
    }

    // TODO: 이후 알림 기능 추가
    @Operation(summary = "댓글 생성 API", description = "댓글을 생성합니다.")
    @PostMapping("/{boardId}/comment")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @PathVariable Long boardId,
            @RequestBody String context
    ) {
        boardService.createComment(tokenUtil.getUserId(), boardId, context);
        return ResponseEntity.ok().body(ApiResponse.success());
    }
  
    @Operation(summary = "댓글 삭제 API", description = "댓글을 삭제합니다.")
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId
    ) {
        boardService.deleteComment(tokenUtil.getUserId(), commentId);
        return ResponseEntity.ok().body(ApiResponse.success());
    }
}
