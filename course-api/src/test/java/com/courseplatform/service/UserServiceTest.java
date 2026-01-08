package com.courseplatform.service;

import com.courseplatform.exception.ResourceNotFoundException;
import com.courseplatform.model.User;
import com.courseplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserByUsername_ShouldReturnUser_WhenExists() {
        // Arrange
        User user = new User();
        user.setUsername("john.doe");
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserByUsername("john.doe");

        // Assert
        assertEquals("john.doe", result.getUsername());
        verify(userRepository).findByUsername("john.doe");
    }

    @Test
    void getUserByUsername_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByUsername("unknown");
        });
    }
}
