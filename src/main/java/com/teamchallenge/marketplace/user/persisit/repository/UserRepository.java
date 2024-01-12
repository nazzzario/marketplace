package com.teamchallenge.marketplace.user.persisit.repository;

import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import org.hibernate.annotations.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByReference(UUID reference);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select (count(u) > 0) from UserEntity u where u.reference = :reference and u.email = :email")
    boolean existsByReferenceAndEmail(@Param("reference") UUID reference,
                                      @Param("email") String email);


    boolean existsByGoogleId(String sub);

    Optional<UserEntity> findByGoogleId(String sub);
}
