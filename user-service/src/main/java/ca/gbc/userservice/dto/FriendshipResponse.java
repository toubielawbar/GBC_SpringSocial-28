package ca.gbc.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendshipResponse {
    private Long friendshipId;
    private Long userId;
    private Long friendId;
}
