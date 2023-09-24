package com.teamchallenge.marketplace.user.persisit.entity;

import com.teamchallenge.marketplace.common.entity.BaseEntity;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Table(name = "tbl_user")
@Entity
@Getter
@Setter
public class UserEntity extends BaseEntity {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String phoneNumber;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<ProductEntity> products;
}
