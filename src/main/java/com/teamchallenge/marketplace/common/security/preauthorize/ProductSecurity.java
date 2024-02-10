package com.teamchallenge.marketplace.common.security.preauthorize;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductSecurity {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public boolean checkOwnership(UUID productReference, String email) {
        var user = userRepository.findByEmail(email).orElseThrow(
                () -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        return user.getRole().equals(RoleEnum.ROOT) ||
                user.getRole().equals(RoleEnum.ADMIN) ||
                productRepository.existsByReferenceAndOwnerEmail(productReference, email);
    }

}
