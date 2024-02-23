package com.teamchallenge.marketplace.admin.service;

import com.teamchallenge.marketplace.admin.dto.FolderComplaintDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AdminProductService {
    Page<UserProductResponseDto> getProductsWithStatusByUser(UUID userReference,
                                                             ProductStatusEnum status,
                                                             Pageable pageable);

    Page<UserProductResponseDto> getFavoriteProductsByUser(UUID userReference, Pageable pageable);

    List<FolderComplaintDto> getComplaintProducts();

    void deleteComplaint(UUID productReference);
}
