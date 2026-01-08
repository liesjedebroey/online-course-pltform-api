package com.courseplatform.service;

import com.courseplatform.model.RefreshToken;
import com.courseplatform.repository.RefreshTokenRepository;
import com.courseplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found")));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        userRepository.findById(userId).ifPresent(refreshTokenRepository::deleteByUser);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

}