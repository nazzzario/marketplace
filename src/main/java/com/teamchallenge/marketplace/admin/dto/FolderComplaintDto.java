package com.teamchallenge.marketplace.admin.dto;

import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;

import java.util.List;

public record FolderComplaintDto(
        ProductResponseDto product,
        List<ComplaintDto> complaints) {
}