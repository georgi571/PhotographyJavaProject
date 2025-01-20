package bg.photographyjava.challenge.service.impl;

import bg.photographyjava.challenge.model.Comment;
import bg.photographyjava.challenge.repository.CommentRepository;
import bg.photographyjava.challenge.service.CommentService;
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
        return this.commentRepository.findById(commentId).orElse(null);
    }


}
