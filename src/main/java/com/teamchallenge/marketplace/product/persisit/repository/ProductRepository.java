package com.teamchallenge.marketplace.product.persisit.repository;

import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>,
                                           PagingAndSortingRepository<ProductEntity, Long>,
                                           JpaSpecificationExecutor<ProductEntity> {
    void deleteByReference(UUID reference);

    @EntityGraph(attributePaths = "images")
    Optional<ProductEntity> findByReference(@NonNull UUID reference);

    Slice<ProductEntity> findByOrderByCreatedDate(Pageable pageable);

    @Query("select (count(p) > 0) from ProductEntity p where p.reference = :reference and p.owner.email = :email")
    boolean existsByReferenceAndOwnerEmail(@Param("reference") UUID reference,
                                           @Param("email") String email);

    @Query("select p from ProductEntity p where p.categoryName = :category and p.status = :status")
    Page<ProductEntity> findByCategoryNameAndStatus(@Param("category") ProductCategoriesEnum categoryName,
                                                    @Param("status") ProductStatusEnum status,
                                                    Pageable pageable);
    @EntityGraph(attributePaths = "images")
    Page<ProductEntity> findByOwnerAndStatus(UserEntity owner, ProductStatusEnum status, Pageable pageable);

    Page<ProductEntity> findByFavoritismId(Long id);
}
