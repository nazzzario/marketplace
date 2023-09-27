package com.teamchallenge.marketplace.product.persisit.entity;

import com.teamchallenge.marketplace.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "tbl_product_images")
@Entity
@Getter
@Setter
public class ProductImage extends BaseEntity {

    private String imageUrl;

    private boolean isCover;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
