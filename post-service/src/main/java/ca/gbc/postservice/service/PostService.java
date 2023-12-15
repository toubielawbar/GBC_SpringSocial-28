package ca.gbc.postservice.service;

import ca.gbc.postservice.dto.CommentResponse;
import ca.gbc.postservice.dto.PostRequest;
import ca.gbc.postservice.dto.PostResponse;
import ca.gbc.postservice.dto.UserResponse;
import ca.gbc.postservice.model.Post;
import ca.gbc.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final PostRepository postRepository;
    private final WebClient webClient;
    @Value("${user.service.url}")
    private String userApiUri;

    @Value("${comment.service.url}")
    private String commentApiUri;
    @Override
    public Post createPost(PostRequest postRequest) {
        log.info("Creating a new post {}", postRequest.getPostContent());

        // Create a new Post entity
        Post post = new Post();
        post.setPostContent(postRequest.getPostContent());
        post.setUsername(postRequest.getUsername());

        // Save the post to the database
        Post savedPost = postRepository.save(post);

        log.info("Post {} is saved with ID: {}", savedPost.getPostContent(), savedPost.getId());

        return savedPost;
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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return webClient.get()
                .uri(commentApiUri+"/{postId}", postId)
                .retrieve()
                .bodyToFlux(CommentResponse.class)
                .collectList()
                .block();
    }

    @Override
    public List<PostResponse> getAllPosts() {
        log.info("Returning a list of posts");

        List<Post> posts = postRepository.findAll();


        return posts.stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    private PostResponse mapToPostResponse(Post post){
        // Get user details based on the post's username
        List<UserResponse> userDetails = getUserByUsername(post.getUsername());

        // Get comments for the post based on its ID
        List<CommentResponse> postComment = getCommentsByPostId(post.getId());

        // Build and return the PostResponse DTO
        return PostResponse.builder()
                .id(post.getId())
                .postContent(post.getPostContent())
                .username(post.getUsername())
                .userDetails(userDetails)
                .postComments(postComment != null ? postComment : new ArrayList<>())
                .build();
    }


    @Override
    public PostResponse getPostById(Long postId) {
        log.info("Getting post with id {}", postId);

        Optional<Post> postID = postRepository.findById(postId);
        if (postID.isPresent()) {
            Post post = postID.get();
            List<UserResponse> userDetails = getUserByUsername(post.getUsername());
            List<CommentResponse> postComment = getCommentsByPostId(post.getId());
            return new PostResponse(post.getId(), post.getPostContent(), post.getUsername(), userDetails, postComment);
        } else {
            return new PostResponse();
        }
    }

    @Override
    public Long updatePost(Long postId, PostRequest postRequest) {
        log.info("Updating a post with id {}", postId);

        Optional<Post> postID = postRepository.findById(postId);

        if (postID.isPresent()) {
            Post post = postID.get();

            post.setPostContent(postRequest.getPostContent());
            post.setUsername(postRequest.getUsername());

            log.info("Post {} is updated",post.getId());

            postRepository.save(post);
            return post.getId();
        } else {
            return postId;
        }
    }

    @Override
    public void deletePost(Long postId) {
        log.info("Post {} is deleted", postId);
        postRepository.deleteById(postId);
    }
}
