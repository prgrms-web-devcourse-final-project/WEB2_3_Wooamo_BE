package com.api.stuv.domain.board.service;

import com.api.stuv.domain.alert.entity.AlertType;
import com.api.stuv.domain.alert.service.AlertService;
import com.api.stuv.domain.board.dto.dto.BoardDetailDTO;
import com.api.stuv.domain.board.dto.response.BoardDetailResponse;
import com.api.stuv.domain.board.dto.request.BoardRequest;
import com.api.stuv.domain.board.dto.response.BoardResponse;
import com.api.stuv.domain.board.dto.request.BoardUpdateRequest;
import com.api.stuv.domain.board.dto.response.CommentResponse;
import com.api.stuv.domain.board.entity.Board;
import com.api.stuv.domain.board.entity.BoardType;
import com.api.stuv.domain.board.entity.Comment;
import com.api.stuv.domain.board.repository.BoardRepository;
import com.api.stuv.domain.board.repository.CommentRepository;
import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.ImageFile;
import com.api.stuv.domain.image.repository.ImageFileRepository;
import com.api.stuv.domain.image.service.ImageService;
import com.api.stuv.domain.image.service.S3ImageService;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.AccessDeniedException;
import com.api.stuv.global.exception.BadRequestException;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import com.api.stuv.global.response.PageResponse;
import com.api.stuv.global.util.common.TemplateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
    private final AlertService alertService;
    private final S3ImageService s3ImageService;

    // TODO: Repository 에서 Service 단으로 데이터 처리 이동
    @Transactional(readOnly = true)
    public PageResponse<BoardResponse> getBoardList(String title, Pageable pageable) {
        return boardRepository.getBoardList(title, pageable);
    }

    @Transactional
    public Map<String, Long> createBoard(Long userId, BoardRequest boardRequest, List<MultipartFile> files) {
        Long boardId = boardRepository.save(BoardRequest.from(userId, boardRequest)).getId();
        if (files != null && !files.isEmpty()) {for (MultipartFile file : files) imageService.handleImage(boardId, file, EntityType.BOARD);}
        return Map.of("boardId", boardId);
    }

    @Transactional(readOnly = true)
    public BoardDetailResponse getBoardDetail(Long boardId) {
        if (!boardRepository.existsById(boardId)) throw new NotFoundException(ErrorCode.BOARD_NOT_FOUND);
        BoardDetailDTO boardDetail = boardRepository.getBoardDetail(boardId);
        List<String> images = boardRepository.getBoardDetailImage(boardId).stream().map(
                filename -> s3ImageService.generateImageFile(EntityType.BOARD, boardId, filename)).toList();
        return new BoardDetailResponse(
                boardDetail.title(),
                boardDetail.userId(),
                boardDetail.nickname(),
                boardDetail.boardType().toString(),
                boardDetail.createdAt().format(TemplateUtils.dateTimeFormatter),
                boardDetail.isConfirm(),
                boardDetail.context(),
                s3ImageService.generateImageFile(EntityType.COSTUME, boardDetail.costumeId(), boardDetail.newFilename()),
                images
        );
    }

    @Transactional
    public Map<String, Long> updateBoard(Long userId, Long boardId, BoardUpdateRequest request, List<MultipartFile> files) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NotFoundException(ErrorCode.BOARD_NOT_FOUND));
        if (!Objects.equals(board.getUserId(), userId)) throw new AccessDeniedException(ErrorCode.BOARD_NOT_AUTHORIZED);
        board.update(request);
        for (String deletedImage : request.deletedImages()) {
            String fileName = deletedImage.substring(deletedImage.lastIndexOf('/') + 1);
            s3ImageService.deleteImageFile(EntityType.BOARD, boardId, fileName);
            imageFileRepository.deleteByNewFilename(fileName);
        }
        if (files != null && !files.isEmpty()) for (MultipartFile file : files) imageService.handleImage(boardId, file, EntityType.BOARD);
        return Map.of("boardId", boardId);
    }

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NotFoundException(ErrorCode.BOARD_NOT_FOUND));
        if (!Objects.equals(board.getUserId(), userId)) throw new AccessDeniedException(ErrorCode.BOARD_NOT_AUTHORIZED);
        List<Comment> comments = commentRepository.findAllByBoardId(boardId);
        List<ImageFile> imageFiles = imageFileRepository.findAllByEntityIdAndEntityType(boardId, EntityType.BOARD);
        if (imageFiles!=null && !imageFiles.isEmpty()) {
            for(ImageFile imageFile : imageFiles) {
                s3ImageService.deleteImageFile(EntityType.BOARD, boardId, imageFile.getNewFilename());
                imageFileRepository.deleteByNewFilename(imageFile.getNewFilename());
            }
        }
        commentRepository.deleteAll(comments);
        boardRepository.delete(board);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
        Board board = boardRepository.findById(comment.getBoardId()).orElseThrow(() -> new NotFoundException(ErrorCode.BOARD_NOT_FOUND));
        if (board.getConfirmedCommentId() != null && board.getConfirmedCommentId().equals(commentId)) throw new BadRequestException(ErrorCode.CONFIRMED_COMMENT);
        if (!Objects.equals(comment.getUserId(), userId)) throw new AccessDeniedException(ErrorCode.COMMENT_NOT_AUTHORIZED);
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentList(Long boardId, Pageable pageable) {
        if (!boardRepository.existsById(boardId)) throw new NotFoundException(ErrorCode.BOARD_NOT_FOUND);
        List<CommentResponse> commentList = commentRepository.getCommentList(boardId, pageable).stream().map(dto -> new CommentResponse(
                dto.commentId(),
                dto.userId(),
                dto.nickname(),
                dto.context(),
                dto.createdAt().format(TemplateUtils.dateTimeFormatter),
                dto.isConfirm() != null && dto.isConfirm().equals(dto.commentId()),
                s3ImageService.generateImageFile(EntityType.COSTUME, dto.costumeId(), dto.newFilename()))).toList();
        return PageResponse.applyPage(commentList, pageable, commentRepository.getCommentCount(boardId));
    }

    @Transactional
    public void createComment(Long userId, Long boardId, String content) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NotFoundException(ErrorCode.BOARD_NOT_FOUND));
        if (!userId.equals(board.getUserId())) alertService.createAlert(board.getUserId(), boardId, AlertType.COMMENT, board.getTitle(), user.getNickname());
        commentRepository.save(Comment.create(boardId, userId, content));
    }

    @Transactional
    public void confirmComment(Long userId, Long commentId) {
        final int CONFIRM_COMMENT_POINT = 5;
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
        Board board = boardRepository.findById(comment.getBoardId()).orElseThrow(() -> new NotFoundException(ErrorCode.BOARD_NOT_FOUND));
        User user = userRepository.findById(comment.getUserId()).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        User boardWitter = userRepository.findById(board.getUserId()).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        if (!board.getBoardType().equals(BoardType.QUESTION)) throw new AccessDeniedException(ErrorCode.BOARD_NOT_QUESTION);
        if (comment.getUserId().equals(userId)) throw new AccessDeniedException(ErrorCode.COMMENT_BY_WRITER);
        if (board.getConfirmedCommentId() != null) throw new AccessDeniedException(ErrorCode.COMMENT_ALREADY_CONFIRM);
        if (!board.getUserId().equals(userId)) throw new AccessDeniedException(ErrorCode.COMMENT_NOT_AUTHORIZED);
        alertService.createAlert(comment.getUserId(), board.getId(), AlertType.CONFIRM, board.getTitle(), boardWitter.getNickname());
        board.confirmComment(commentId);
        user.updatePoint(BigDecimal.valueOf(CONFIRM_COMMENT_POINT));
    }
}
