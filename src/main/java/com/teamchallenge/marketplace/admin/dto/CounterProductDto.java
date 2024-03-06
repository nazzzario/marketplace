package com.teamchallenge.marketplace.admin.dto;

import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;


public record CounterProductDto(
        long ownerId,
        ProductStatusEnum status,
        long count,
        long view,
        long raise,
        long favoritism
)  {

}
