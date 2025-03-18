package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void 유저_데이터_100만건_생성() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded password");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        for(long i = 1; i <= 1_000_000; i++) {
            SignupRequest request = new SignupRequest(
                    "a" + i + "@a.com",
                    "test" + i,
                    "Asdf1234!",
                    "ROLE_USER"
            );
            authService.signup(request);
        }

        // then
        verify(userRepository, times(1_000_000)).save(any(User.class));
    }
}
