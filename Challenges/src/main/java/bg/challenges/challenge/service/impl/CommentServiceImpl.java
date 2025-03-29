package bg.challenges.challenge.service.impl;

import bg.challenges.challenge.model.Comment;
import bg.challenges.challenge.repository.CommentRepository;
import bg.challenges.challenge.service.CommentService;
import bg.challenges.exception.ResourceNotFoundException;
import bg.challenges.exception.UnauthorizedActionException;
import bg.challenges.web.dto.CommentReportResponse;
import bg.challenges.web.mapper.DtoMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void saveComment(Comment comment) {
        this.commentRepository.saveAndFlush(comment);
    }

    @Override
    public Comment getCommentById(UUID commentId) {
        return this.commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    @Override
    public void deleteComment(UUID commentId, UUID userId, Authentication authentication) {

        Comment comment = this.getCommentById(commentId);

        boolean isOwner = comment.getAuthorId().equals(userId);
        boolean hasPermission = hasPermission(authentication);

        if (!isOwner && !hasPermission) {
            throw new UnauthorizedActionException("User is not authorized to delete this comment");
        }

        comment.setDeleted(true);
        this.saveComment(comment);
    }

    @Override
    public CommentReportResponse getReportedComment(UUID id) {
        Comment comment = this.getCommentById(id);
        return DtoMapper.mapCommentToCommentReportTResponse(comment);
    }

    private boolean hasPermission(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("PERMISSION_" + "deleteMessage"));
    }
}
