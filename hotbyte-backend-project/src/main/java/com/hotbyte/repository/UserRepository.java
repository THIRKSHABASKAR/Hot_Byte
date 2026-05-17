package com.hotbyte.repository;

import com.hotbyte.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository
        extends JpaRepository<User, Long> {

    // Find user by email for login
    Optional<User> findByEmail(String email);

    // Check if email already registered
    boolean existsByEmail(String email);
}