package org.example.expert.domain.user.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;

@Getter
@RequiredArgsConstructor
public class UserResponse {

    private final Long id;
    private final String email;
    private final String nickname;

    public static UserResponse of(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }
}
