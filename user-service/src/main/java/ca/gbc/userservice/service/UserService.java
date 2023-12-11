package ca.gbc.userservice.service;

import ca.gbc.userservice.dto.UserRequest;
import ca.gbc.userservice.dto.UserResponse;
import ca.gbc.userservice.model.User;
import ca.gbc.userservice.repository.UserRepository;
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
public class UserService implements IUserService {
    private final UserRepository userRepository;
    @Override
    public User createUser(UserRequest userRequest) {
        log.info("Creating a new user {}", userRequest.getName());

        // Create a new User entity
        User user = new User();
        user.setName(userRequest.getName());
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());

        // Save the user to the database
        User savedUser = userRepository.save(user);

        log.info("User {} is saved with ID: {}", savedUser.getName(), savedUser.getId());

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

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        log.info("Getting user with id {}", userId);

        Optional<User> userID = userRepository.findById(userId);
        if (userID.isPresent()) {
            User user = userID.get();
            return new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getEmail());
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

            log.info("User {} is updated",user.getId());

            userRepository.save(user);
            return user.getId();
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
