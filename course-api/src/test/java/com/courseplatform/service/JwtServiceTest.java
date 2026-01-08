package com.courseplatform.service;

import com.courseplatform.model.Role;
import com.courseplatform.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Gebruik een base64 string van minimaal 256 bits (bijv. "vS8yL...=" )
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L); // 1 uur
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        User user = new User();
        user.setUsername("test.user");
        user.setRole(Role.STUDENT);

        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);

        assertEquals("test.user", username);
    }

    @Test
    void isTokenValid_ShouldReturnTrueForCorrectUser() {
        User user = new User();
        user.setUsername("test.user");
        user.setRole(Role.STUDENT);

        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, user);

        assertTrue(isValid);
    }
}
