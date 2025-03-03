package com.api.stuv.domain.board.service;

import com.api.stuv.domain.alert.service.AlertService;
import com.api.stuv.domain.board.entity.Board;
import com.api.stuv.domain.board.entity.BoardType;
import com.api.stuv.domain.board.entity.Comment;
import com.api.stuv.domain.board.repository.BoardRepository;
import com.api.stuv.domain.board.repository.CommentRepository;
import com.api.stuv.domain.user.entity.RoleType;
import com.api.stuv.domain.user.entity.User;
import com.api.stuv.domain.user.repository.UserRepository;
import com.api.stuv.global.exception.AccessDeniedException;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ConfirmCommentTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BoardService boardService;

    @Mock
    private AlertService alertService;

    List<User> users = List.of(
            new User(1L, "user1", "user1", null, null, "user1", BigDecimal.valueOf(0), null, RoleType.USER, null),
            new User(2L, "user2", "user2", null, null, "user2", BigDecimal.valueOf(0), null, RoleType.USER, null)
    );

    List<Board> boards = List.of(
            new Board(1L, "title1", "context1", BoardType.QUESTION),
            new Board(2L, "title2", "context2", BoardType.FREE),
            createBoardWithId(3L,"title1", "context1", 1L, BoardType.QUESTION),
            new Board(2L, "title1", "context1", BoardType.QUESTION)
    );

    List<Comment> comments = List.of(
            createCommentWithId(1L, 1L, "context1", 1L),
            createCommentWithId(2L, 1L, "context1", 2L),
            createCommentWithId(3L, 1L, "context2", 2L),
            createCommentWithId(4L, 2L, "context1", 2L)
    );

    @Test
    void successConfirmComment() {
        // given
        User writer = users.get(0);
        User commentWriter = users.get(1);
        Board board = boards.get(0);
        Comment comment = comments.get(1);


        when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));
        when(boardRepository.findById(comment.getBoardId())).thenReturn(Optional.of(board));
        when(userRepository.findById(board.getUserId())).thenReturn(Optional.of(writer));
        when(userRepository.findById(comment.getUserId())).thenReturn(Optional.of(commentWriter));

        // when
        boardService.confirmComment(writer.getId(), comment.getId());

        // then
        assertThat(board.getConfirmedCommentId()).isEqualTo(comment.getId());
        assertThat(commentWriter.getPoint()).isEqualTo(BigDecimal.valueOf(5));
    }

    @Test
    void failConfirmCommentWhenCommentNotFound() {
        // given
        User writer = users.get(0);
        User commentWriter = users.get(1);
        Board board = boards.get(0);
        Comment comment = comments.get(1);

        when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));
        when(boardRepository.findById(comment.getBoardId())).thenReturn(Optional.of(board));
        when(userRepository.findById(board.getUserId())).thenReturn(Optional.of(writer));
        when(userRepository.findById(comment.getUserId())).thenReturn(Optional.of(commentWriter));

        // when
        try {
            boardService.confirmComment(writer.getId(), 2L);
        } catch (NotFoundException e) {
            // then
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NOT_FOUND);
        }
    }

    @Test
    void failConfirmCommentWhenBoardNotFound() {
        // given
        User writer = users.get(0);
        User commentWriter = users.get(1);
        Board board = boards.get(0);
        Comment comment = comments.get(1);

        when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));
        when(boardRepository.findById(comment.getBoardId())).thenReturn(Optional.of(board));
        when(userRepository.findById(board.getUserId())).thenReturn(Optional.of(writer));
        when(userRepository.findById(comment.getUserId())).thenReturn(Optional.of(commentWriter));

        // when
        try {
            boardService.confirmComment(writer.getId(), comment.getId());
        } catch (NotFoundException e) {
            // then
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.BOARD_NOT_FOUND);
        }
    }

    @Test
    void failConfirmCommentWhenBoardIsNotQuestion() {
        // given
        User writer = users.get(1);
        User commentWriter = users.get(0);
        Board board = boards.get(1);
        Comment comment = comments.get(0);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(boardRepository.findById(comment.getBoardId())).thenReturn(Optional.of(board));
        when(userRepository.findById(board.getUserId())).thenReturn(Optional.of(writer));
        when(userRepository.findById(comment.getUserId())).thenReturn(Optional.of(commentWriter));
        // when
        try {
            boardService.confirmComment(writer.getId(), comment.getId());
        } catch (AccessDeniedException e) {
            // then
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.BOARD_NOT_QUESTION);
        }
    }

    @Test
    void failConfirmCommentWhenCommentIsWrittenByWriter() {
        // given
        User writer = users.get(0);
        User commentWriter = users.get(0);
        Board board = boards.get(0);
        Comment comment = comments.get(0);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(boardRepository.findById(comment.getBoardId())).thenReturn(Optional.of(board));
        when(userRepository.findById(board.getUserId())).thenReturn(Optional.of(writer));
        when(userRepository.findById(comment.getUserId())).thenReturn(Optional.of(commentWriter));

        // when
        try {
            boardService.confirmComment(writer.getId(), comment.getId());
        } catch (AccessDeniedException e) {
            // then
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.COMMENT_BY_WRITER);
        }
    }

    @Test
    void failConfirmCommentWhenCommentAlreadyConfirmed() {
        // given
        User writer = users.get(0);
        User commentWriter = users.get(1);
        Board board = boards.get(0);
        Comment comment = comments.get(1);

        board.confirmComment(comment.getId());

        when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));
        when(boardRepository.findById(comment.getBoardId())).thenReturn(Optional.of(board));
        when(userRepository.findById(board.getUserId())).thenReturn(Optional.of(writer));
        when(userRepository.findById(comment.getUserId())).thenReturn(Optional.of(commentWriter));

        // when
        try {
            boardService.confirmComment(writer.getId(), comment.getId());
        } catch (AccessDeniedException e) {
            // then
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.COMMENT_ALREADY_CONFIRM);
        }
    }

    @Test
    void failConfirmCommentWhenUserIsNotAuthorized() {
        // given
        User writer = users.get(1);
        User commentWriter = users.get(0);
        Board board = boards.get(3);
        Comment comment = comments.get(0);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(boardRepository.findById(comment.getBoardId())).thenReturn(Optional.of(board));
        when(userRepository.findById(board.getUserId())).thenReturn(Optional.of(writer));
        when(userRepository.findById(comment.getUserId())).thenReturn(Optional.of(commentWriter));

        // when
        try {
            boardService.confirmComment(writer.getId(), comment.getId());
        } catch (AccessDeniedException e) {
            // then
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NOT_AUTHORIZED);
        }
    }


    private Comment createCommentWithId(Long id, Long boardId, String context, Long userId) {
        Comment comment = Comment.create(boardId, userId, context);
        setCommentId(comment, id);
        return comment;
    }

    private void setCommentId(Comment comment, Long id) {
        try {
            Field field = Comment.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(comment, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Board createBoardWithId(Long confirmCommentId, String title, String context, Long userId, BoardType boardType) {
        Board board = new Board(userId, title, context, boardType);
        setBoardId(board, confirmCommentId);
        return board;
    }

    private void setBoardId(Board board, Long id) {
        try {
            Field field = Board.class.getDeclaredField("confirmedCommentId");
            field.setAccessible(true);
            field.set(board, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
