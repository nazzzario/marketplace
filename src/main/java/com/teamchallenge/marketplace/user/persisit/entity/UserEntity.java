package com.teamchallenge.marketplace.user.persisit.entity;

import com.teamchallenge.marketplace.common.entity.BaseEntity;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "tbl_user")
@Entity
@Getter
@Setter
public class UserEntity extends BaseEntity implements UserDetails {

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

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<ProductEntity> products;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername(){
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
