package com.courseplatform.service;

import com.courseplatform.exception.ResourceNotFoundException;
import com.courseplatform.model.Role;
import com.courseplatform.model.User;
import com.courseplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    // Optioneel: handig voor je profiel-pagina in de frontend later
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User changeUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setRole(newRole);
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Cannot delete. User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
}