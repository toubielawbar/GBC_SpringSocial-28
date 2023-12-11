package ca.gbc.postservice.service;

import ca.gbc.postservice.dto.PostRequest;
import ca.gbc.postservice.dto.PostResponse;
import ca.gbc.postservice.model.Post;

import java.util.List;

public interface IPostService {
    Post createPost(PostRequest postRequest);
    List<PostResponse> getAllPosts();
    PostResponse getPostById(Long postId);
    Long updatePost(Long postId, PostRequest postRequest);
    void deletePost(Long postId);
}
