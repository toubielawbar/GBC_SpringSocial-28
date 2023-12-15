package ca.gbc.friendshipservice.service;

import ca.gbc.friendshipservice.dto.FriendshipRequest;
import ca.gbc.friendshipservice.dto.FriendshipResponse;
import ca.gbc.friendshipservice.model.Friendship;
import ca.gbc.friendshipservice.repository.FriendshipRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;

    public Friendship createFriend(FriendshipRequest friendshipRequest) {
        log.info("Creating a new friendship {}", friendshipRequest.getFriendshipId());

        // Create a new friendship entity
        Friendship friendship = new Friendship();
        friendship.setUserId(friendshipRequest.getUserId());
        friendship.setFriendId(friendshipRequest.getFriendId());

        // Save the friendship to the database
        Friendship savedFriendship = friendshipRepository.save(friendship);

        log.info("User friendship {} is saved with ID: {}", savedFriendship.getUserId(), savedFriendship.getFriendId());

        return savedFriendship;
    }

    public List<FriendshipResponse> getFriends(Long userId) {
        List<Friendship> friends = friendshipRepository.findByUserId(userId);
        return mapFriendshipsToResponse(friends);
    }

    public List<FriendshipResponse> getFriendsByUserId(Long userId) {
        List<Friendship> friendships = friendshipRepository.findByUserId(userId);

        if (!friendships.isEmpty()) {
            return friendships.stream()
                    .map(friendship -> {
                        return new FriendshipResponse(friendship.getFriendshipId(), friendship.getUserId(), friendship.getFriendId());
                    })
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private List<FriendshipResponse> mapFriendshipsToResponse(List<Friendship> friendships) {

        return friendships.stream()
                .map(friendship -> {
                    FriendshipResponse friendshipResponse = new FriendshipResponse();
                    friendshipResponse.setUserId(friendship.getUserId());
                    friendshipResponse.setFriendId(friendship.getFriendId());
                    return friendshipResponse;
                })
                .toList();
    }

    public void deleteFriend(Long friendshipId) {
        log.info("user {} is deleted", friendshipId);
        friendshipRepository.deleteById(friendshipId);
    }
}
