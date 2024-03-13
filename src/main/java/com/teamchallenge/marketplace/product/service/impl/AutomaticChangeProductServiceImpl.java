package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.email.service.EmailService;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.AutomaticChangeProductService;
import com.teamchallenge.marketplace.product.service.UserProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutomaticChangeProductServiceImpl implements AutomaticChangeProductService {
    private static final String RAISE_ADD_PREFIX = "raiseAd_";
    private static final String VIEWS_KEY = "productViews";
    private static final String CONDITIONS = "<h3>Відповідно до умов користування сайтом.</h3>";
    private static final String UL_CLOSE = "</ul>";
    private static final String UL = "<ul>";
    private static final String LI = "<li>";
    private static final String LI_CLOSE = "</li>";

    @Value("${product.delete.periodDeadline}")
    private int periodDeleteDeadline;
    @Value("${product.periodWarning}")
    private int periodWarning;
    @Value("${product.disable.size}")
    private long sizeProductDisabled;
    @Value("${product.active.periodsDeadline}")
    private int periodActiveDeadline;
    @Value("${mail.template.path.user}")
    private String userTemplatePath;


    private final ProductRepository productRepository;
    private final UserProductService productService;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Select all expired products with users. For each user,
     * we check whether there is space in the archive, if there is no space,
     * we delete older products.
     * For use: cron = "${product.cron}".
     */
    @Scheduled(cron = Scheduled.CRON_DISABLED)
    @Async
    @Override
    public void changeStatusFromActiveToDisabled() {

        var activeProducts = productRepository
                .findByStatusAndTimePeriodAndPublishDateBefore(ProductStatusEnum.ACTIVE,
                        periodActiveDeadline, getDeadlineDate(periodActiveDeadline));

        var userDisabledProduct = productRepository.findByProductDisabledIn(
                        activeProducts, ProductStatusEnum.DISABLED, sizeProductDisabled)
                .stream().collect(Collectors.groupingBy(ProductEntity::getOwner));

        var userActiveProducts = activeProducts.stream().collect(
                Collectors.groupingBy(ProductEntity::getOwner));

        userDisabledProduct.forEach((user, list) -> {
            long sizeActive = userActiveProducts.entrySet().stream()
                    .filter(e -> e.getKey().getEmail().equals(user.getEmail()))
                    .mapToLong(e -> e.getValue().size()).sum();

            deleteOldEntity(list,
                    (sizeActive + list.size() - sizeProductDisabled),
                    user.getEmail());
        });

        userActiveProducts.forEach(this::processChangeStatus);
    }

    private void processChangeStatus(UserEntity user, List<ProductEntity> products) {

        StringBuilder message = new StringBuilder();

        message.append("<h2>Ми перевели статус ваших повідомлень з активного в архів:</h2>")
                .append(UL);

        products.forEach(product -> {
            productService.getProductAndChangeStatus(product, ProductStatusEnum.DISABLED,
                    periodDeleteDeadline);
            message.append(LI).append(product.getProductDescription()).append(LI_CLOSE);
        });

        message.append(UL_CLOSE).append(CONDITIONS);
        emailService.sendEmail(user.getEmail(), emailService.buildMsgForUser(userTemplatePath, message.toString()),
                "Автоматичний переведення просрочених повідомлень");

    }

    private void deleteOldEntity(List<ProductEntity> products, long sizeDeleteEntity, String email) {
        if (sizeDeleteEntity > 0) {
            StringBuilder message = new StringBuilder();

            message.append("<h2>Ми видалили з архіву ваші старі повідомлення:</h2>")
                    .append(UL);
            products.stream().sorted(Comparator.comparing(ProductEntity::getPublishDate))
                    .limit(sizeDeleteEntity).forEach(product -> {
                        productService.changeDeleteProduct(product);
                        message.append(LI).append(product.getProductDescription()).append(LI_CLOSE);
                    });
            message.append("<h3>Це потрібно для внесення в архів нових повідомлень</h3>");
            emailService.sendEmail(email, emailService.buildMsgForUser(userTemplatePath, message.toString()),
                    "Автоматичне видалення зайвих повідомлень");
        }
    }

    private LocalDate getDeadlineDate(int days) {
        return LocalDate.now().minusDays(days);
    }

    /**
     * Delete older product with deadline date in the archive.
     * For use: cron = "${product.cron}".
     */
    @Scheduled(cron = Scheduled.CRON_DISABLED)
    @Async
    @Override
    public void deleteDisabledOldProduct() {
        var userDisabledProducts = productRepository
                .findByStatusAndPublishDateBefore(ProductStatusEnum.DISABLED,
                        getDeadlineDate(periodDeleteDeadline)).stream().collect(Collectors
                        .groupingBy(ProductEntity::getOwner));

        userDisabledProducts.forEach((key, value) -> processDeleteOldEntities(key.getEmail(), value));
    }

    private void processDeleteOldEntities(String email, List<ProductEntity> disabledProducts) {
        StringBuilder message = new StringBuilder();

        message.append("<h2>Ми видалили з архіву ваші старі повідомлення:</h2>")
                .append(UL);
        disabledProducts.forEach(product -> {
            productService.changeDeleteProduct(product);
            message.append(LI).append(product.getProductDescription()).append(LI_CLOSE);
        });
        message.append(UL_CLOSE).append(CONDITIONS);

        emailService.sendEmail(email, emailService.buildMsgForUser(userTemplatePath, message.toString()),
                "Автоматичний видалення просрочених повідомлень");
    }

    @Scheduled(cron = "${product.raise.cron}")
    @Override
    public void updateDatabaseFromRaiseAd() {
        Map<Object, Object> adRaiseMap = redisTemplate.opsForHash().entries(RAISE_ADD_PREFIX);

        if (!adRaiseMap.isEmpty()) {
            for (Map.Entry<Object, Object> entry : adRaiseMap.entrySet()) {
                UUID productUUID = UUID.fromString((String) entry.getKey());
                int count = Integer.parseInt((String) entry.getValue());

                Optional<ProductEntity> byReference = productRepository
                        .findByReference(productUUID);
                if (byReference.isPresent()) {
                    ProductEntity productEntity = byReference.get();
                    productEntity.setAdRaiseCount(productEntity.getAdRaiseCount() + count);
                    productRepository.save(productEntity);
                }
            }
        }

        redisTemplate.delete(RAISE_ADD_PREFIX);
    }

    @Scheduled(cron = "${product.view.cron}")
    @Override
    public void updateDatabaseFromView() {
        Map<Object, Object> viewsMap = redisTemplate.opsForHash().entries(VIEWS_KEY);

        if (!viewsMap.isEmpty()) {
            for (Map.Entry<Object, Object> entry : viewsMap.entrySet()) {
                UUID productUUID = UUID.fromString((String) entry.getKey());
                int views = Integer.parseInt((String) entry.getValue());

                Optional<ProductEntity> byReference = productRepository.findByReference(productUUID);
                if (byReference.isPresent()) {
                    ProductEntity productEntity = byReference.get();
                    productEntity.setViewCount(productEntity.getViewCount() + views);
                    productRepository.save(productEntity);
                }
            }
        }

        redisTemplate.delete(VIEWS_KEY);
    }
}