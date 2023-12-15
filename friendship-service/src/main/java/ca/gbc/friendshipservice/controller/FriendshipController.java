package ca.gbc.friendshipservice.controller;

import ca.gbc.friendshipservice.dto.FriendshipRequest;
import ca.gbc.friendshipservice.dto.FriendshipResponse;
import ca.gbc.friendshipservice.model.Friendship;
import ca.gbc.friendshipservice.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friendship")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Friendship createFriendship(@RequestBody FriendshipRequest friendshipRequest) {
        return friendshipService.createFriend(friendshipRequest);
    }


    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<FriendshipResponse>> getFriendsByUser(@PathVariable Long userId) {
        List<FriendshipResponse> friendshipResponses = friendshipService.getFriendsByUserId(userId);
        return ResponseEntity.ok(friendshipResponses);
    }

    @DeleteMapping({"/{friendshipId}"})
    public ResponseEntity<?>deleteUser(@PathVariable("friendshipId") Long friendshipId){
        friendshipService.deleteFriend(friendshipId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
