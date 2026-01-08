package com.courseplatform.controller;

import com.courseplatform.dto.AuthResponse;
import com.courseplatform.dto.LoginRequest;
import com.courseplatform.dto.RegisterRequest;
import com.courseplatform.model.User;
import com.courseplatform.service.AuthService;
import com.courseplatform.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @GetMapping("/test-token")
    public String getTestToken(@RequestParam String name) {
        // We maken een tijdelijke test-gebruiker aan
        UserDetails testUser = new org.springframework.security.core.userdetails.User(
                name,
                "password",
                new java.util.ArrayList<>()
        );

        // We genereren een token met jouw nieuwe veilige sleutel
        return jwtService.generateToken(testUser);
    }

    // Endpoint for user registration: POST http://localhost:8080/api/auth/register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // Endpoint for login: POST http://localhost:8080/api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

}
