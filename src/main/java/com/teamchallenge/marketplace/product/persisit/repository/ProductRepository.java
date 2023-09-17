package com.teamchallenge.marketplace.product.persisit.repository;

import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByReference(@NonNull UUID reference);

}
