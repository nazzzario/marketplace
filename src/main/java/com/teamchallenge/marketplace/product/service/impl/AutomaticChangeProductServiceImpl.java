package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.email.service.EmailService;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.AutomaticChangeProductService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutomaticChangeProductServiceImpl implements AutomaticChangeProductService {
    @Value("${product.periodDeadline}")
    private long periodDeadline;
    @Value("${product.sizeProductDisabled}")
    private long sizeProductDisabled;

    private final ProductRepository productRepository;
    private final EmailService emailService;

    public void changeStatusFromActiveToDisabled(){
        var userActiveProducts = productRepository
                .findByStatusAndPublishDateBefore(ProductStatusEnum.ACTIVE,
                        getDeadlineDate()).stream().collect(Collectors
                        .groupingBy(ProductEntity::getOwner));

        var userDisabledProduct = productRepository.findByStatusAndOwnerIn(
                ProductStatusEnum.DISABLED, userActiveProducts.keySet())
                .stream().collect(Collectors.groupingBy(ProductEntity::getOwner));



        userDisabledProduct.forEach((key, value) -> processChangeStatusAndDeleteOldEntities(
                key.getEmail(), userActiveProducts.get(key), value,
                (value.size() + userActiveProducts.get(key).size() - sizeProductDisabled)));

    }

    public void deleteDisabledOldProduct(){
        var userDisabledProducts = productRepository
                .findByStatusAndPublishDateBefore(ProductStatusEnum.DISABLED,
                        getDeadlineDate()).stream().collect(Collectors
                        .groupingBy(ProductEntity::getOwner));

        userDisabledProducts.forEach((key, value) -> processDeleteOldEntities(key.getEmail(), value));
    }

    private void processDeleteOldEntities(String email, List<ProductEntity> disabledProducts) {
        StringBuilder message = new StringBuilder();

        message.append("Ми видалили з архіву ваші старі повідомлення:").append("\n");
        disabledProducts.forEach(product -> {
            productRepository.delete(product);
            message.append(product.getProductDescription()).append("\n");
        });
        message.append(". Відповідно до умов користування сайтом.").append("\n");

        emailService.sendEmail(email, emailService.buildMsgForUser(message.toString()),
                "Автоматичний видалення просрочених повідомлень");
    }

    private void processChangeStatusAndDeleteOldEntities(@NotNull String email, List<ProductEntity> activeProductEntities,
                                                         List<ProductEntity> disabledEntities, long sizeDeleteEntity) {
        StringBuilder message = new StringBuilder();

        if (sizeDeleteEntity > 0){
            message.append("Ми видалили з архіву ваші старі повідомлення:").append("\n");
            disabledEntities.stream().sorted(Comparator.comparing(ProductEntity::getPublishDate))
                .limit(sizeDeleteEntity).forEach(product -> {
                    productRepository.delete(product);
                    message.append(product.getProductDescription()).append("\n");
                });
            message.append("Це потрібно для внесення в архів нових повідомлень").append("\n");
        }

        message.append("Ми перевели статус ваших повідомлень з активного в архів:").append("\n");
        activeProductEntities.forEach(product ->{
            product.setStatus(ProductStatusEnum.DISABLED);
            productRepository.save(product);
        });

        message.append(". Відповідно до умов користування сайтом.").append("\n");
        emailService.sendEmail(email, emailService.buildMsgForUser(message.toString()),
                "Автоматичний переведення просрочених повідомлень та видалення зайвих");
    }



    private LocalDate getDeadlineDate() {
        return LocalDate.now().minusDays(periodDeadline);
    }
}
