package bg.challenges.challenge.service.impl;

import bg.challenges.challenge.model.Comment;
import bg.challenges.challenge.repository.CommentRepository;
import bg.challenges.exception.ResourceNotFoundException;
import bg.challenges.exception.UnauthorizedActionException;
import bg.challenges.web.dto.CommentReportResponse;
import bg.challenges.web.mapper.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplUTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CommentServiceImpl commentService;

    private UUID commentId;
    private UUID userId;
    private Comment comment;

    @BeforeEach
    void setUp() {
        commentId = UUID.randomUUID();
        userId = UUID.randomUUID();

        comment = new Comment();
        comment.setId(commentId);
        comment.setAuthorId(userId);
        comment.setDeleted(false);
    }

    @Test
    void testSaveComment() {
        commentService.saveComment(comment);
        verify(commentRepository, times(1)).saveAndFlush(comment);
    }

    @Test
    void testGetCommentById_WhenCommentExists() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment foundComment = commentService.getCommentById(commentId);

        assertNotNull(foundComment);
        assertEquals(commentId, foundComment.getId());
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void testGetCommentById_WhenCommentNotFound_ShouldThrowException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(commentId));
    }

    @Test
    void testDeleteComment_WhenUserIsOwner_ShouldMarkAsDeleted() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(commentId, userId, authentication);

        assertTrue(comment.isDeleted());
        verify(commentRepository, times(1)).saveAndFlush(comment);
    }

    @Test
    void testDeleteComment_WhenUserIsNotOwnerAndHasNoPermission_ShouldThrowException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(UnauthorizedActionException.class, () -> commentService.deleteComment(commentId, UUID.randomUUID(), authentication));
    }

    @Test
    void testGetReportedComment() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        try (MockedStatic<DtoMapper> mockedDtoMapper = mockStatic(DtoMapper.class)) {
            CommentReportResponse mockResponse = new CommentReportResponse();
            mockedDtoMapper.when(() -> DtoMapper.mapCommentToCommentReportTResponse(comment)).thenReturn(mockResponse);

            CommentReportResponse result = commentService.getReportedComment(commentId);

            assertNotNull(result);
            verify(commentRepository, times(1)).findById(commentId);
            mockedDtoMapper.verify(() -> DtoMapper.mapCommentToCommentReportTResponse(comment), times(1));
        }
    }
}