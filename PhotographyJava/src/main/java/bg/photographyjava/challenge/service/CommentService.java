package bg.photographyjava.challenge.service;

import bg.photographyjava.challenge.model.Comment;

import java.util.UUID;

public interface CommentService {
    void saveComment(Comment comment);

    Comment getCommentById(UUID commentId);
}
