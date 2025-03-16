package bg.challenges.challenge.service;

import bg.challenges.challenge.model.Comment;

import java.util.UUID;

public interface CommentService {
    void saveComment(Comment comment);

    Comment getCommentById(UUID commentId);
}
