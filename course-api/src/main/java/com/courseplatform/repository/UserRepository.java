package com.courseplatform.repository;

import com.courseplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    //Required for authentication fo find user by username
    Optional<User> findByUsername(String username);

    //Used to check if email/username is already taken during registration
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
