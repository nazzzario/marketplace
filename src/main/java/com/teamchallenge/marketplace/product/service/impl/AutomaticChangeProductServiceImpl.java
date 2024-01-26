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
    private static final String CONDITIONS = "<h3>Відповідно до умов користування сайтом.</h3>";
    private static final String UL_CLOSE = "</ul>";
    private static final String UL = "<ul>";
    private static final String LI = "<li>";
    private static final String LI_CLOSE = "</li>";

    @Value("${product.delete.periodDeadline}")
    private int periodDeadline;
    @Value("${product.periodWarning}")
    private int periodWarning;
    @Value("${product.disable.size}")
    private long sizeProductDisabled;
    @Value("${product.active.periodsDeadline}")
    private int[] periodsDeadline;

    private final ProductRepository productRepository;
    private final UserProductService productService;
    private final EmailService emailService;

    /**
     * Select all expired products with users. For each user,
     * we check whether there is space in the archive, if there is no space,
     * we delete older products.
     * For use: cron = "${product.cron}".
     * */
    @Scheduled(cron = Scheduled.CRON_DISABLED)
    @Async
    @Override
    public void changeStatusFromActiveToDisabled(){
        var userActiveProducts = Arrays.stream(periodsDeadline).boxed()
                .flatMap(days -> productRepository
                .findByStatusAndTimePeriodAndPublishDateBefore(ProductStatusEnum.ACTIVE,
                        days, getDeadlineDate(days)).stream()).collect(Collectors
                .groupingBy(ProductEntity::getOwner));

        if (!userActiveProducts.isEmpty()) {
            userActiveProducts.forEach(this::processChangeStatus);
        }
    }

    private void processChangeStatus(UserEntity user, List<ProductEntity> products) {
        long countDisabled = productRepository.countByOwnerAndStatus(user,
                ProductStatusEnum.DISABLED);
        if(countDisabled > 0 && (products.size() + countDisabled > sizeProductDisabled)){
            deleteOldEntity(productRepository.findByStatusAndOwner(ProductStatusEnum.DISABLED,
                    user), (products.size() + countDisabled - sizeProductDisabled),
                    user.getEmail());
        }

        StringBuilder message = new StringBuilder();

        message.append("<h2>Ми перевели статус ваших повідомлень з активного в архів:</h2>")
                .append(UL);
        products.forEach(product ->{
            productService.getProductAndChangeStatus(product, ProductStatusEnum.DISABLED);
            message.append(LI).append(product.getProductDescription()).append(LI_CLOSE);
        });

        message.append(UL_CLOSE).append(CONDITIONS);
        emailService.sendEmail(user.getEmail(), emailService.buildMsgForUser(message.toString()),
                "Автоматичний переведення просрочених повідомлень");

    }

    private void deleteOldEntity(List<ProductEntity> products, long sizeDeleteEntity, String email) {
        if (sizeDeleteEntity > 0){
            StringBuilder message = new StringBuilder();

            message.append("<h2>Ми видалили з архіву ваші старі повідомлення:</h2>")
                    .append(UL);
            products.stream().sorted(Comparator.comparing(ProductEntity::getPublishDate))
                    .limit(sizeDeleteEntity).forEach(product -> {
                        productService.processDeleteProduct(product);
                        message.append(LI).append(product.getProductDescription()).append(LI_CLOSE);
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
     * For use: cron = "${product.cron}".
     * */
    @Scheduled(cron = Scheduled.CRON_DISABLED)
    @Async
    @Override
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
                .append(UL);
        disabledProducts.forEach(product -> {
            productService.processDeleteProduct(product);
            message.append(LI).append(product.getProductDescription()).append(LI_CLOSE);
        });
        message.append(UL_CLOSE).append(CONDITIONS);

        emailService.sendEmail(email, emailService.buildMsgForUser(message.toString()),
                "Автоматичний видалення просрочених повідомлень");
    }

    /**
     * Warning about delete older product for the period before deadline date in the archive.
     * For use: cron = "${product.cron}".
     * */
    @Scheduled(cron = Scheduled.CRON_DISABLED)
    @Async
    @Override
    public void deleteWarningOldEntity(){
        var userDisabledProducts = productRepository
                .findByStatusAndPublishDate(ProductStatusEnum.DISABLED,
                        getDeadlineDate(periodDeadline - periodWarning)).stream().collect(Collectors
                        .groupingBy(ProductEntity::getOwner));

        userDisabledProducts.forEach((key, value) -> processWarningDeleteOldEntities(key.getEmail(), value));
    }


    private void processWarningDeleteOldEntities(String email, List<ProductEntity> products) {
        StringBuilder message = new StringBuilder();

        message.append("<h2>Ми попереджаємо про видалення з архіву через ")
                .append(periodWarning).append(" днів(день -ня) ваших старих повідомленнь:</h2>")
                .append(UL);

        products.forEach(product -> message.append(LI).append(product.getProductDescription())
                .append(LI_CLOSE));

        message.append(UL_CLOSE).append(CONDITIONS);

        emailService.sendEmail(email, emailService.buildMsgForUser(message.toString()),
                "Попередження про автоматичний видалення просрочених повідомлень");
    }

    /**
     * Warning about change product for the period before deadline date in the archive.
     * For use: cron = "${product.cron}".
     * */
    @Scheduled(cron = Scheduled.CRON_DISABLED)
    @Async
    @Override
    public void changeWarningStatusEntity() {
        var userActiveProducts = Arrays.stream(periodsDeadline).boxed()
                .flatMap(days -> productRepository
                        .findByStatusAndTimePeriodAndPublishDate(ProductStatusEnum.ACTIVE,
                                days, getDeadlineDate(days - periodWarning)).stream()).collect(Collectors
                        .groupingBy(ProductEntity::getOwner));

        userActiveProducts.forEach((key, value) -> processWarningChangeStatus(
                key.getEmail(), value));
    }

    private void processWarningChangeStatus(String email, List<ProductEntity> products) {
        StringBuilder message = new StringBuilder();

        message.append("<h2>Ми попереджаємо про переведення через ").append(periodWarning)
                .append(" днів(день -ня) ваших активних повідомленнь в архів:</h2>")
                .append(UL);

        products.forEach(product -> message.append(LI).append(product.getProductDescription())
                .append(LI_CLOSE));

        message.append(UL_CLOSE).append(CONDITIONS);

        emailService.sendEmail(email, emailService.buildMsgForUser(message.toString()),
                "Попередження про автоматичний переведення просрочених активних повідомлень");
    }
}
