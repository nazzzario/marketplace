package com.teamchallenge.marketplace.product.service;

public interface AutomaticChangeProductService {
    void changeStatusFromActiveToDisabled();
    void deleteDisabledOldProduct();
    void updateDatabaseFromRaiseAd();
    void updateDatabaseFromView();
}
