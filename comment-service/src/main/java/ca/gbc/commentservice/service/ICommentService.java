package ca.gbc.commentservice.service;

import ca.gbc.commentservice.dto.CommentRequest;
import ca.gbc.commentservice.dto.CommentResponse;
import ca.gbc.commentservice.model.Comment;

import java.util.List;

public interface ICommentService {
    Comment createComment(CommentRequest commentRequest);
    List<CommentResponse> getAllComments();
    CommentResponse getCommentById(Long commentId);
    Long updateComment(Long commentId, CommentRequest commentRequest);
    void deleteComment(Long commentId);
}
