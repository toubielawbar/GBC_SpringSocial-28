package ca.gbc.commentservice.service;

import ca.gbc.commentservice.dto.CommentRequest;
import ca.gbc.commentservice.dto.CommentResponse;
import ca.gbc.commentservice.dto.UserResponse;
import ca.gbc.commentservice.model.Comment;
import ca.gbc.commentservice.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor

public class CommentService implements ICommentService{
    private final CommentRepository commentRepository;
    private final WebClient webClient;
    @Value("${user.service.url}")
    private String userApiUri;
    @Override
    public Comment createComment(CommentRequest commentRequest) {
        log.info("Creating a new comment {}", commentRequest.getCommentContent());

        // Create a new Comment entity
        Comment comment = new Comment();
        comment.setCommentContent(commentRequest.getCommentContent());
        comment.setUsername(commentRequest.getUsername());
        comment.setPostId(commentRequest.getPostId());

        // Save the comment to the database
        Comment savedComment = commentRepository.save(comment);

        log.info("Comment {} is saved with ID: {}", savedComment.getCommentContent(), savedComment.getCommentId());

        return savedComment;
    }

    public List<UserResponse> getUserByUsername(String username) {
        return webClient.get()
                .uri(userApiUri+"/{username}", username)
                .retrieve()
                .bodyToFlux(UserResponse.class)
                .collectList()
                .block();
    }

    public List<UserResponse> getAllUsers() {
        return webClient.get()
                .uri("/api/user/all")
                .retrieve()
                .bodyToFlux(UserResponse.class)
                .collectList()
                .block();
    }

    @Override
    public List<CommentResponse> getAllComments() {
        log.info("Returning a list of comments");

        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapToCommentResponse(Comment comment){
        List<UserResponse> userDetails = getUserByUsername(comment.getUsername());

        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .commentContent(comment.getCommentContent())
                .username(comment.getUsername())
                .postId(comment.getPostId())
                .userDetails(userDetails)
                .build();
    }

    @Override
    public CommentResponse getCommentById(Long commentId) {
        log.info("Getting comment with id {}", commentId);

        Optional<Comment> commentID = commentRepository.findById(commentId);
        if (commentID.isPresent()) {
            Comment comment = commentID.get();
            List<UserResponse> userDetails = getUserByUsername(comment.getUsername());
            return new CommentResponse(comment.getCommentId(), comment.getCommentContent(), comment.getUsername(), comment.getPostId(), userDetails);
        } else {
            return new CommentResponse();
        }
    }


    public List<CommentResponse> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        if (!comments.isEmpty()) {
            return comments.stream()
                    .map(comment -> {
                        List<UserResponse> userDetails = getUserByUsername(comment.getUsername());
                        return new CommentResponse(comment.getCommentId(), comment.getCommentContent(), comment.getUsername(), comment.getPostId(), userDetails);
                    })
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Long updateComment(Long commentId, CommentRequest commentRequest) {
        log.info("Updating a comment with id {}", commentId);

        Optional<Comment> commentID = commentRepository.findById(commentId);

        if (commentID.isPresent()) {
            Comment comment = commentID.get();

            comment.setCommentContent(commentRequest.getCommentContent());
            comment.setUsername(commentRequest.getUsername());

            log.info("Comment {} is updated",comment.getCommentId());

            commentRepository.save(comment);
            return comment.getCommentId();
        } else {
            return commentId;
        }
    }

    @Override
    public void deleteComment(Long commentId) {
        log.info("Comment {} is deleted", commentId);
        commentRepository.deleteById(commentId);
    }
}
