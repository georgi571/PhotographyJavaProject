package bg.challenges.challenge.service;

import bg.challenges.challenge.model.Comment;
import bg.challenges.web.dto.ChallengeDetailsResponse;
import bg.challenges.web.dto.CommentReportResponse;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface CommentService {
    void saveComment(Comment comment);

    Comment getCommentById(UUID commentId);

    void deleteComment(UUID commentId, UUID userId, Authentication authentication);

    CommentReportResponse getReportedComment(UUID id);
}
