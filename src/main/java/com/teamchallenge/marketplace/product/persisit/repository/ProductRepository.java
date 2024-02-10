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

import java.time.LocalDate;
import java.util.*;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>,
        PagingAndSortingRepository<ProductEntity, Long>,
        JpaSpecificationExecutor<ProductEntity> {

    /**
     * Get products with status disabled by owner with products with status active
     * and limit in archive
     */
    String SQL_REQUEST = """
            select distinct p from ProductEntity p where p.status = :status and p.owner in
            (select pd.owner from ProductEntity pd where pd.status = :status
            group by pd.owner having count(pd) > 0 and (count(pd) +
            (select count(pa) from ProductEntity pa where pa.owner = pd.owner and
            pa in :products group by pa.owner) - :size) > 0)
            """;

    void deleteByReference(UUID reference);

    @Query("select distinct p from ProductEntity p order by (p.viewCount + p.adRaiseCount) desc")
    @EntityGraph(attributePaths = {"owner", "images"})
    Page<ProductEntity> getAllByAllCountDesc(Pageable pageable);

    @Query("select distinct p from ProductEntity p order by (p.viewCount + p.adRaiseCount) asc")
    @EntityGraph(attributePaths = {"owner", "images"})
    Page<ProductEntity> getAllByAllCountAsc(Pageable pageable);

    @EntityGraph(attributePaths = "images")
    Optional<ProductEntity> findByReference(@NonNull UUID reference);

    Slice<ProductEntity> findByOrderByCreatedDate(Pageable pageable);

    @Query("select (count(p) > 0) from ProductEntity p where p.reference = :reference and p.owner.email = :email")
    boolean existsByReferenceAndOwnerEmail(@Param("reference") UUID reference,
                                           @Param("email") String email);

    @EntityGraph(attributePaths = "images")
    Page<ProductEntity> findByOwnerAndStatus(UserEntity owner, ProductStatusEnum status, Pageable pageable);

    @Query("select p from ProductEntity p where p.categoryName = :category and p.status = :status")
    Page<ProductEntity> findByCategoryNameAndStatus(@Param("category") ProductCategoriesEnum categoryName,
                                                    @Param("status") ProductStatusEnum status,
                                                    Pageable pageable);

    @EntityGraph(attributePaths = "images")
    Page<ProductEntity> findByFavoritismId(Long id, Pageable pageable);

    @EntityGraph(attributePaths = {"owner", "images"})
    List<ProductEntity> findByStatusAndPublishDateBefore(ProductStatusEnum status, LocalDate deadlineDate);

    long countByOwnerAndStatus(UserEntity userEntity, ProductStatusEnum status);

    @EntityGraph(attributePaths = "owner")
    List<ProductEntity> findByStatusAndTimePeriodAndPublishDateBefore(ProductStatusEnum status, Integer days, LocalDate deadlineDate);

    List<ProductEntity> findByStatusAndOwner(ProductStatusEnum status, UserEntity user);

    @EntityGraph(attributePaths = {"owner", "images"})
    @Query(SQL_REQUEST)
    List<ProductEntity> findByProductDisabledIn(@Param("products") List<ProductEntity> activeProducts,
                                                @Param("status") ProductStatusEnum status,
                                                @Param("size") long size);

    @EntityGraph(attributePaths = "images")
    List<ProductEntity> findByOwner(UserEntity user);
}
