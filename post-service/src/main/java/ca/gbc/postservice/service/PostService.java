package ca.gbc.postservice.service;

import ca.gbc.postservice.dto.PostRequest;
import ca.gbc.postservice.dto.PostResponse;
import ca.gbc.postservice.model.Post;
import ca.gbc.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;
    @Override
    public Post createPost(PostRequest postRequest) {
        log.info("Creating a new post {}", postRequest.getPostContent());

        // Create a new Post entity
        Post post = new Post();
        post.setPostContent(postRequest.getPostContent());
        post.setUserId(postRequest.getUserId());

        // Save the post to the database
        Post savedPost = postRepository.save(post);

        log.info("Post {} is saved with ID: {}", savedPost.getPostContent(), savedPost.getId());

        return savedPost;
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

        return PostResponse.builder()
                .id(post.getId())
                .postContent(post.getPostContent())
                .userId(post.getUserId())
                .build();
    }

    @Override
    public PostResponse getPostById(Long postId) {
        log.info("Getting post with id {}", postId);

        Optional<Post> postID = postRepository.findById(postId);
        if (postID.isPresent()) {
            Post post = postID.get();
            return new PostResponse(post.getId(), post.getPostContent(), post.getUserId());
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
            post.setUserId(postRequest.getUserId());

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
