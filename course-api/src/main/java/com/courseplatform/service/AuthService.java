package com.courseplatform.service;

import com.courseplatform.dto.AuthResponse;
import com.courseplatform.dto.LoginRequest;
import com.courseplatform.dto.RegisterRequest;
import com.courseplatform.exception.UnauthorizedActionException;
import com.courseplatform.model.User;
import com.courseplatform.model.RefreshToken;
import com.courseplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService; // Toevoegen!

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUserName())) {
            throw new UnauthorizedActionException("Username '" + request.getUserName() + "' is already taken.");
        }

        User newUser = new User();
        newUser.setUsername(request.getUserName());
        newUser.setEmail(request.getEmail());
        newUser.setRole(request.getRole());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(newUser);
        return "User registered successfully!";
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> new UnauthorizedActionException("Invalid username or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedActionException("Invalid username or password");
        }

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(String requestToken) {
        return refreshTokenService.findByToken(requestToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(user);
                    return AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(requestToken)
                            .username(user.getUsername())
                            .role(user.getRole().name())
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
}