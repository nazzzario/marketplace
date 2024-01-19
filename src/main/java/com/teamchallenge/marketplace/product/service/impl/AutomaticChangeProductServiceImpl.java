package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.email.service.EmailService;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.AutomaticChangeProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutomaticChangeProductServiceImpl implements AutomaticChangeProductService {
    @Value("${product.periodDeadline}")
    private int periodDeadline;
    @Value("${product.sizeProductDisabled}")
    private long sizeProductDisabled;
    @Value("${product.periodsDeadline}")
    private int[] periodsDeadline;

    private final ProductRepository productRepository;
    private final EmailService emailService;

    /**
     * Select all expired products with users. For each user,
     * we check whether there is space in the archive, if there is no space,
     * we delete older products.
     * For test: cron = "${product.cron}".
     * */
    @Scheduled(cron = Scheduled.CRON_DISABLED)
    @Async
    public void changeStatusFromActiveToDisabled(){
        var userActiveProducts = Arrays.stream(periodsDeadline).boxed()
                .flatMap(days -> productRepository
                .findByStatusAndTimePeriodAndPublishDateBefore(ProductStatusEnum.ACTIVE,
                        days, getDeadlineDate(days)).stream()).collect(Collectors
                .groupingBy(ProductEntity::getOwner));

        if (userActiveProducts.isEmpty()) return;

        var userDisabledProduct = productRepository.findByStatusAndOwnerIn(
                ProductStatusEnum.DISABLED, userActiveProducts.keySet())
                .stream().collect(Collectors.groupingBy(ProductEntity::getOwner));

        if (!userDisabledProduct.isEmpty()){
            userDisabledProduct.forEach((key, value) -> deleteOldEntity(value,
                    (userActiveProducts.get(key).size() + value.size() - sizeProductDisabled),
                    key.getEmail()));
        }

        userActiveProducts.forEach((key, value) -> processChangeStatus(
                key.getEmail(), value));
    }

    private void processChangeStatus(String email, List<ProductEntity> products) {
        StringBuilder message = new StringBuilder();

        message.append("<h2>Ми перевели статус ваших повідомлень з активного в архів:</h2>")
                .append("<ul>");
        products.forEach(product ->{
            product.setStatus(ProductStatusEnum.DISABLED);
            message.append("<li>").append(product.getProductDescription()).append("</li>");
            productRepository.save(product);
        });

        message.append("</ul>").append("<h3>Відповідно до умов користування сайтом.</h3>");
        emailService.sendEmail(email, emailService.buildMsgForUser(message.toString()),
                "Автоматичний переведення просрочених повідомлень");

    }

    private void deleteOldEntity(List<ProductEntity> products, long sizeDeleteEntity, String email) {
        if (sizeDeleteEntity > 0){
            StringBuilder message = new StringBuilder();

            message.append("<h2>Ми видалили з архіву ваші старі повідомлення:</h2>")
                    .append("<ul>");
            products.stream().sorted(Comparator.comparing(ProductEntity::getPublishDate))
                    .limit(sizeDeleteEntity).forEach(product -> {
                        productRepository.delete(product);
                        message.append("<li>").append(product.getProductDescription()).append("</li>");
                    });
            message.append("<h3>Це потрібно для внесення в архів нових повідомлень</h3>");
            emailService.sendEmail(email, emailService.buildMsgForUser(message.toString()),
                    "Автоматичне видалення зайвих повідомлень");
        }
    }

    private LocalDate getDeadlineDate(int days) {
        return LocalDate.now().minusDays(days);
    }

    /**
     * Delete older product with deadline date in the archive.
     * For test: cron = "${product.cron}".
     * */
    @Scheduled(cron = Scheduled.CRON_DISABLED)
    @Async
    public void deleteDisabledOldProduct(){
        var userDisabledProducts = productRepository
                .findByStatusAndPublishDateBefore(ProductStatusEnum.DISABLED,
                        getDeadlineDate(periodDeadline)).stream().collect(Collectors
                        .groupingBy(ProductEntity::getOwner));

        userDisabledProducts.forEach((key, value) -> processDeleteOldEntities(key.getEmail(), value));
    }

    private void processDeleteOldEntities(String email, List<ProductEntity> disabledProducts) {
        StringBuilder message = new StringBuilder();

        message.append("<h2>Ми видалили з архіву ваші старі повідомлення:</h2>")
                .append("<ul>");
        disabledProducts.forEach(product -> {
            productRepository.delete(product);
            message.append("<li>").append(product.getProductDescription()).append("/li>");
        });
        message.append("</ul>").append("<h3>Відповідно до умов користування сайтом.</h3>");

        emailService.sendEmail(email, emailService.buildMsgForUser(message.toString()),
                "Автоматичний видалення просрочених повідомлень");
    }
}
