package com.teamchallenge.marketplace.user.persisit.repository;

import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByReference(UUID reference);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
}
