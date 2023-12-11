package ca.gbc.postservice.repository;

import ca.gbc.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
