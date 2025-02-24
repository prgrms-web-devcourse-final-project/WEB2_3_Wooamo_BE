package com.api.stuv.domain.board.service;

import com.api.stuv.domain.board.dto.BoardDetailResponse;
import com.api.stuv.domain.board.dto.BoardRequest;
import com.api.stuv.domain.board.dto.BoardResponse;
import com.api.stuv.domain.board.dto.CommentResponse;
import com.api.stuv.domain.board.entity.Board;
import com.api.stuv.domain.board.entity.BoardType;
import com.api.stuv.domain.board.entity.Comment;
import com.api.stuv.domain.board.repository.BoardRepository;
import com.api.stuv.domain.board.repository.CommentRepository;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.ImageFile;
import com.api.stuv.domain.image.repository.ImageFileRepository;
import com.api.stuv.domain.image.service.ImageService;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.AccessDeniedException;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ImageFileRepository imageFileRepository;
    private final ImageService imageService;

    // TODO : 이후 이미지 다운로드 기능 추가해 주세요!
    @Transactional(readOnly = true)
    public PageResponse<BoardResponse> getBoardList(String title, Pageable pageable) {
        return boardRepository.getBoardList(title, pageable, "http://localhost:8080/api/v1/board/image/");
    }

    @Transactional
    public Map<String, Long> createBoard(Long userId, BoardRequest boardRequest, List<MultipartFile> files) {
        Long boardId = boardRepository.save(BoardRequest.from(userId, boardRequest)).getId();
        if (files != null && !files.isEmpty()) { for (MultipartFile file : files) { imageService.handleImage(boardId, file, EntityType.BOARD); }}
        return Map.of("boardId", boardId);
    }

    // TODO : 이후 이미지 기능 추가!
    @Transactional(readOnly = true)
    public BoardDetailResponse getBoardDetail(Long boardId) {
        return boardRepository.getBoardDetail(boardId);
    }

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NotFoundException(ErrorCode.BOARD_NOT_FOUND));
        if (!Objects.equals(board.getUserId(), userId)) throw new AccessDeniedException(ErrorCode.BOARD_NOT_AUTHORIZED);
        List<Comment> comments = commentRepository.findAllByBoardId(boardId);
        List<ImageFile> imageFiles = imageFileRepository.findAllByEntityIdAndEntityType(boardId, EntityType.BOARD);
        if ( !imageFiles.isEmpty() ) {
            // S3 이미지 삭제 작업
            imageFileRepository.deleteAll(imageFiles);
        }
        commentRepository.deleteAll(comments);
        boardRepository.delete(board);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
        if (!Objects.equals(comment.getUserId(), userId)) throw new AccessDeniedException(ErrorCode.COMMENT_NOT_AUTHORIZED);
        commentRepository.delete(comment);
    }

    // TODO : 이후 이미지 다운로드 기능 추가해 주세요!
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentList(Long boardId, Pageable pageable) {
        if ( !boardRepository.existsById(boardId) ) throw new NotFoundException(ErrorCode.BOARD_NOT_FOUND);
        return commentRepository.getCommentList(boardId, pageable, "http://localhost:8080/api/v1/costume/");
    }

    // TODO: 이후 알림 기능 추가
    @Transactional
    public void createComment(Long userId, Long boardId, String content) {
        if (!boardRepository.existsById(boardId)) throw new NotFoundException(ErrorCode.BOARD_NOT_FOUND);
        commentRepository.save(Comment.create(userId, boardId, content));
    }

    // TODO: 이후 알림 기능 추가
    @Transactional
    public void confirmComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
        Board board = boardRepository.findById(comment.getBoardId()).orElseThrow(() -> new NotFoundException(ErrorCode.BOARD_NOT_FOUND));
        if (!board.getBoardType().equals(BoardType.QUESTION)) throw new AccessDeniedException(ErrorCode.BOARD_NOT_QUESTION);
        if (comment.getUserId().equals(userId)) throw new AccessDeniedException(ErrorCode.COMMENT_BY_WRITER);
        if (board.getConfirmedCommentId() != null) throw new AccessDeniedException(ErrorCode.COMMENT_ALREADY_CONFIRM);
        if (!board.getUserId().equals(userId)) throw new AccessDeniedException(ErrorCode.COMMENT_NOT_AUTHORIZED);
        board.confirmComment(commentId);
    }
}
