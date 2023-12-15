package ca.gbc.commentservice.controller;


import ca.gbc.commentservice.dto.CommentRequest;
import ca.gbc.commentservice.dto.CommentResponse;
import ca.gbc.commentservice.model.Comment;
import ca.gbc.commentservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    // create comment
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@RequestBody CommentRequest commentRequest) {
        return commentService.createComment(commentRequest);
    }
    // read comments
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getAllComments(){
        return commentService.getAllComments();
    }

    // read comment


    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CommentResponse>> getCommentByPost(@PathVariable Long postId) {
        List<CommentResponse> commentResponses = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(commentResponses);
    }


    // update comment
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest) {
        commentService.updateComment(commentId, commentRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // delete comment
    @DeleteMapping({"/{commentId}"})
    public ResponseEntity<?>deleteComment(@PathVariable("commentId") Long commentId){
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
