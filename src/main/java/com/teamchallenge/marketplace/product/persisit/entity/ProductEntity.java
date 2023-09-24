package com.teamchallenge.marketplace.product.persisit.entity;

import com.teamchallenge.marketplace.common.entity.BaseEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Table(name = "tbl_product")
@Entity
@Getter
@Setter
public class ProductEntity extends BaseEntity {

    // TODO: 9/16/23 add change category enum to entity
    @Enumerated(EnumType.STRING)
    private ProductCategoriesEnum categoryName;

    private String city;

    @NotNull
    private String productTitle;

    @NotNull
    private String productDescription;

    @Enumerated(EnumType.STRING)
    private ProductStateEnum state;

    @Enumerated(EnumType.STRING)
    private ProductStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;
}
