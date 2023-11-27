package com.teamchallenge.marketplace.common.dalaloader.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.service.ProductService;
import com.teamchallenge.marketplace.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductLoader implements Consumer<List<Map<String, Object>>> {
    private final UserService  userService;
    private final ProductService productService;

    @Override
    public void accept(List<Map<String, Object>> maps) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        maps.stream().filter(x -> x.containsKey("products"))
                .forEach(x ->
                        ((List<HashMap>) x.get("products")).forEach(y -> {
                            ProductRequestDto productRequestDto = mapper.convertValue(y, ProductRequestDto.class);
                            UUID reference = userService.getUserByPhoneNumber("+380987654321").reference();

                            productService.createProduct(productRequestDto, reference);
                        })
                );




    }
}
