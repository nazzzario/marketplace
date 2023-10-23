package com.teamchallenge.marketplace.common.security.preauthorize;

import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductSecurity {

    private final ProductRepository productRepository;

    public boolean checkOwnership(UUID productReference, String email){
        return productRepository.existsByReferenceAndOwnerEmail(productReference, email);
    }

}
