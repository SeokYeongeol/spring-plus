package org.example.expert.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChangeProfileResponse {

    private final Long id;
    private final String email;
    private final String nickname;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String profileImage;

    public static UserChangeProfileResponse of(User user) {
        return new UserChangeProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage()
        );
    }
}
