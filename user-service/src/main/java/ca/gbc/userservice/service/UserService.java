package ca.gbc.userservice.service;

import ca.gbc.userservice.dto.FriendshipResponse;
import ca.gbc.userservice.dto.UserRequest;
import ca.gbc.userservice.dto.UserResponse;
import ca.gbc.userservice.model.User;
import ca.gbc.userservice.repository.UserRepository;
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
public class UserService implements IUserService {
    private final UserRepository userRepository;

    private final WebClient webClient;
    @Value("${friendship.service.url}")
    private String friendshipApiUri;
    @Override
    public User createUser(UserRequest userRequest) {
        log.info("Creating a new user {}", userRequest.getName());

        User user = new User();
        user.setName(userRequest.getName());
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());

        // Save the user to the database
        User savedUser = userRepository.save(user);

        log.info("User {} is saved with ID: {}", savedUser.getName(), savedUser.getUserId());

        return savedUser;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.info("Returning a list of users");

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToUserResponse(User user){
        List<FriendshipResponse> friends = getUserFriends(user.getUserId());
        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .friends(friends != null ? friends : new ArrayList<>())
                .build();
    }

//    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<FriendshipResponse> getUserFriends(Long userId) {
        return webClient.get()
                .uri(friendshipApiUri+"/{userId}", userId)
                .retrieve()
                .bodyToFlux(FriendshipResponse.class)
                .collectList()
                .block();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        log.info("Getting user with id {}", userId);

        Optional<User> userID = userRepository.findById(userId);
        if (userID.isPresent()) {
            User user = userID.get();
            List<FriendshipResponse> friends = getUserFriends(user.getUserId());
            return new UserResponse(user.getUserId(), user.getName(), user.getUsername(), user.getEmail(), friends);
        } else {
            return new UserResponse();
        }
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        log.info("Getting user with username {}", username);

        Optional<User> uname = userRepository.findByUsername(username);
        if (uname.isPresent()) {
            User user = uname.get();
            List<FriendshipResponse> friends = getUserFriends(user.getUserId());
            return new UserResponse(user.getUserId(), user.getName(), user.getUsername(), user.getEmail(), friends);
        } else {
            return new UserResponse();
        }
    }

    @Override
    public Long updateUser(Long userId, UserRequest userRequest) {
        log.info("Updating a user with id {}", userId);

        Optional<User> userID = userRepository.findById(userId);

        if (userID.isPresent()) {
            User user = userID.get();

            user.setName(userRequest.getName());
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());

            log.info("User {} is updated",user.getUserId());

            userRepository.save(user);
            return user.getUserId();
        } else {
            return userId;
        }
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("user {} is deleted", userId);
        userRepository.deleteById(userId);
    }
}
