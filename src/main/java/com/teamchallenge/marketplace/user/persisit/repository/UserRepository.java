package com.teamchallenge.marketplace.user.persisit.repository;

import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByReference(UUID reference);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    Optional<UserEntity> findByEmail(String email);

    Page<UserEntity> findDistinctByProductsStatus(ProductStatusEnum status, Pageable pageable);

    boolean existsByEmail(String email);

    @Query("select (count(u) > 0) from UserEntity u where u.reference = :reference and u.email = :email")
    boolean existsByReferenceAndEmail(@Param("reference") UUID reference,
                                      @Param("email") String email);


    boolean existsByEmailAndProductsReference(String email, UUID productReference);

    boolean existsByEmailAndProductsImagesId(String email, Long imageId);

    List<UserEntity> findByFavoriteProducts(ProductEntity productEntity);

    long countByRole(RoleEnum roleEnum);

    boolean existsByEmailAndProductsReferenceAndProductsStatus(String name,
                                                               UUID productReference,
                                                               ProductStatusEnum productStatusEnum);

    Optional<UserEntity> findByProductsReference(UUID reference);
}
