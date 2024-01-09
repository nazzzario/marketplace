package com.teamchallenge.marketplace.product.service;

import java.util.UUID;

public interface UserFavoriteProductService {
    void addProductToFavorites(UUID productReference);

    void removeProductFromFavorites(UUID productReference);
}
