package com.courseplatform.service;

import com.courseplatform.dto.AuthResponse;
import com.courseplatform.dto.LoginRequest;
import com.courseplatform.dto.RegisterRequest;
import com.courseplatform.model.Role;
import com.courseplatform.model.User;
import com.courseplatform.model.RefreshToken;
import com.courseplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.STUDENT);

        testRefreshToken = new RefreshToken();
        testRefreshToken.setToken("mock-uuid-token");
        testRefreshToken.setUser(testUser);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("mock-jwt-token");
        when(refreshTokenService.createRefreshToken("testuser")).thenReturn(testRefreshToken);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getAccessToken());
        assertEquals("mock-uuid-token", response.getRefreshToken());
        assertEquals("testuser", response.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void register_NewUser_ShouldSaveUser() {
        // Arrange
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setUserName("newuser");
        regRequest.setPassword("pass");
        regRequest.setRole(Role.STUDENT);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        // Act
        String result = authService.register(regRequest);

        // Assert
        assertEquals("User registered successfully!", result);
        verify(userRepository).save(any(User.class));
    }
}