package org.example.expert.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserChangeProfileRequest {

    @NotBlank
    private String nickname;
}
