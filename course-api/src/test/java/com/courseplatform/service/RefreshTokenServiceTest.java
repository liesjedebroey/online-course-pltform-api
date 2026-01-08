package com.courseplatform.service;

import com.courseplatform.model.RefreshToken;
import com.courseplatform.model.User;
import com.courseplatform.repository.RefreshTokenRepository;
import com.courseplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        // Stel handmatig de waarde in die normaal uit application.properties komt
        ReflectionTestUtils.setField(refreshTokenService, "refreshExpirationMs", 604800000L);
    }

    @Test
    void verifyExpiration_ShouldThrowException_WhenExpired() {
        // Arrange
        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setExpiryDate(LocalDateTime.now().minusDays(1)); // Gisteren verlopen

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            refreshTokenService.verifyExpiration(expiredToken);
        });
        verify(refreshTokenRepository).delete(expiredToken);
    }

    @Test
    void createRefreshToken_ShouldReturnNewToken() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(username);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNotNull(result.getToken()); // Check of er een UUID is gegenereerd
        assertTrue(result.getExpiryDate().isAfter(LocalDateTime.now()));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

}
