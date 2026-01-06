package com.courseplatform.service;

import com.courseplatform.dto.AuthResponse;
import com.courseplatform.dto.LoginRequest;
import com.courseplatform.dto.RegisterRequest;
import com.courseplatform.exception.UnauthorizedActionException;
import com.courseplatform.model.User;
import com.courseplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    //private final JwtService jwtService;

    public String register(RegisterRequest request) {
        // Validation: check if user exists
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

    public String login(LoginRequest request) {
        //Basic check before we add JWT logic
        User user = userRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> new UnauthorizedActionException("Invalid username or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedActionException("Invalid username or password");
        }

        // TODO: Generate and return JWT token here
        return "Login successful for " + user.getUsername();
    }

}
