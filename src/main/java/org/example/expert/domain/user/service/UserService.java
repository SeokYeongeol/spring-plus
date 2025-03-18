package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.uploader.S3Uploader;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.request.UserChangeProfileRequest;
import org.example.expert.domain.user.dto.response.UserChangeProfileResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname());
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(int page, int size, String query) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<User> users = query == null
                ? userRepository.findAll(pageable)
                : userRepository.findAllByNickname(query, pageable);

        return users.map(UserResponse::of);
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    @Transactional
    public UserChangeProfileResponse changeProfile(
            Long userId,
            UserChangeProfileRequest dto,
            MultipartFile file
    ) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User Not Found"));

        String imageUrl = findUser.getProfileImage();
        if(file != null && !file.isEmpty()) {
            imageUrl = s3Uploader.uploadFile(file);
        }
        findUser.changeProfile(dto.getNickname(), imageUrl);

        return UserChangeProfileResponse.of(findUser);
     }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }
}
