package ca.gbc.friendshipservice.repository;

import ca.gbc.friendshipservice.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByUserId(Long userId);
}
