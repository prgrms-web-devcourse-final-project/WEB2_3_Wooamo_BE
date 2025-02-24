package com.api.stuv.domain.board.service;

import com.api.stuv.domain.board.dto.BoardRequest;
import com.api.stuv.domain.board.dto.BoardResponse;
import com.api.stuv.domain.board.entity.Board;
import com.api.stuv.domain.board.entity.Comment;
import com.api.stuv.domain.board.dto.CommentResponse;
import com.api.stuv.domain.board.repository.BoardRepository;
import com.api.stuv.domain.board.repository.CommentRepository;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.ImageFile;
import com.api.stuv.domain.image.repository.ImageFileRepository;
import com.api.stuv.domain.image.service.ImageService;
import com.api.stuv.domain.image.service.S3ImageService;
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
    private final ImageService imageService;
    private final S3ImageService s3ImageService;
    private final ImageFileRepository imageFileRepository;

    // TODO : 이후 이미지 다운로드 기능 추가해 주세요!
    @Transactional(readOnly = true)
    public PageResponse<BoardResponse> getBoardList(String title, Pageable pageable) {
        return boardRepository.getBoardList(title, pageable, "http://localhost:8080/api/v1/board/image/");
    }

    @Transactional
    public Map<String, Long> createBoard(Long userId, BoardRequest boardRequest, List<MultipartFile> files) {
        Long boardId = boardRepository.save(BoardRequest.from(userId, boardRequest)).getId();
        if (files != null && !files.isEmpty()) { for (MultipartFile file : files) { handleImage(boardId, file); }}
        return Map.of("boardId", boardId);
    }

    public void handleImage(Long boardId, MultipartFile file) {
        String fullFileName = imageService.getFileName(file);
        s3ImageService.uploadImageFile(file, EntityType.BOARD, boardId, fullFileName);
        ImageFile imageFile = ImageFile.createImageFile(file.getOriginalFilename(), fullFileName, boardId, EntityType.BOARD);
        imageFileRepository.save(imageFile);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        if ( !userRepository.existsById(userId) ) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
        if (!Objects.equals(comment.getUserId(), userId)) throw new AccessDeniedException(ErrorCode.COMMENT_NOT_AUTHORIZED);
        commentRepository.delete(comment);
    }

    // TODO : 이후 이미지 다운로드 기능 추가해 주세요!
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentList(Long boardId, Pageable pageable) {
        if (!boardRepository.existsById(boardId)) throw new NotFoundException(ErrorCode.BOARD_NOT_FOUND);
        return commentRepository.getCommentList(boardId, pageable, "http://localhost:8080/api/v1/costume/");
    }

    // TODO: 이후 알림 기능 추가
    @Transactional
    public void createComment(Long userId, Long boardId, String content) {
        if (!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        if (!boardRepository.existsById(boardId)) throw new NotFoundException(ErrorCode.BOARD_NOT_FOUND);
        commentRepository.save(Comment.create(userId, boardId, content));
    }
}
