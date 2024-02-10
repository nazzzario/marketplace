package com.teamchallenge.marketplace.user.persisit.entity;

import com.teamchallenge.marketplace.common.entity.BaseEntity;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "tbl_user")
@Entity
@Getter
@Setter
public class UserEntity extends BaseEntity {

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    private boolean isNonLocked;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<ProductEntity> products;

    @ManyToMany
    @JoinTable(name = "tbl_user_favorite_products",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<ProductEntity> favoriteProducts = new HashSet<>();
}
