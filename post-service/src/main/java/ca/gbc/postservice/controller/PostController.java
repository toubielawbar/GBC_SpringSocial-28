package ca.gbc.postservice.controller;

import ca.gbc.postservice.dto.PostRequest;
import ca.gbc.postservice.dto.PostResponse;
import ca.gbc.postservice.model.Post;
import ca.gbc.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    // create post
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@RequestBody PostRequest postRequest) {
        return postService.createPost(postRequest);
    }
    // read posts
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getAllPosts(){
        return postService.getAllPosts();
    }

    // read post


    // update post
    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long postId, @RequestBody PostRequest postRequest) {
        postService.updatePost(postId, postRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // delete post
    @DeleteMapping({"/{postId}"})
    public ResponseEntity<?>deletePost(@PathVariable("postId") Long postId){
        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
