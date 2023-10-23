package com.teamchallenge.marketplace.product.persisit.repository;

import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, PagingAndSortingRepository<ProductEntity, Long> {
    void deleteByReference(UUID reference);

    Optional<ProductEntity> findByReference(@NonNull UUID reference);

    List<ProductEntity> findByProductTitleLikeIgnoreCase(String productTitle);

    Slice<ProductEntity> findByOrderByCreatedDate(Pageable pageable);

    @Query("select (count(p) > 0) from ProductEntity p where p.reference = :reference and p.owner.email = :email")
    boolean existsByReferenceAndOwnerEmail(@Param("reference") UUID reference,
                                           @Param("email") String email);
}
