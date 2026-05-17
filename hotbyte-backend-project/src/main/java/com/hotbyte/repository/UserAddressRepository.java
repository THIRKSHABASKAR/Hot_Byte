package com.hotbyte.repository;

import com.hotbyte.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository
        extends JpaRepository<UserAddress, Long> {

    // Get all addresses of a user
    List<UserAddress> findByUserId(Long userId);

    // Get default address of a user
    Optional<UserAddress> findByUserIdAndIsDefaultTrue(
            Long userId);

    // Count addresses of a user
    int countByUserId(Long userId);
}