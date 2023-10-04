package com.teamchallenge.marketplace.product.persisit.repository;

import com.teamchallenge.marketplace.product.persisit.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
}
