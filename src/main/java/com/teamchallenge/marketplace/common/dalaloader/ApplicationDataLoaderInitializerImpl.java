package com.teamchallenge.marketplace.common.dalaloader;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.WaitStrategies;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Profile({"!dev"})
@Component
@FieldDefaults(level = PRIVATE)
public class ApplicationDataLoaderInitializerImpl implements ApplicationDataLoaderInitializer {

    @Autowired
    ApplicationDataLoader applicationDataLoader;

    boolean initialized = false;

    final Lock lock = new ReentrantLock();


    @Override
    public boolean isInitialized() {
        lock.lock();

        try {
            return initialized;
        } finally {
            lock.unlock();
        }
    }

    @Async
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Retryer<Void> retryer = RetryerBuilder.<Void>newBuilder()
                .retryIfException()
                .withWaitStrategy(WaitStrategies.fixedWait(60, TimeUnit.SECONDS))
                .build();

        try {
            retryer.call(() -> {
                try {
                    log.info("dataloader.ApplicationDataLoaderInitializer starting.");
                    applicationDataLoader.load();

                    lock.lock();
                    try {
                        initialized = true;
                    } finally {
                        lock.unlock();
                    }
                    log.info("dataloader.ApplicationDataLoaderInitializer finished.");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw e;
                }
                return null;
            });
        } catch (ExecutionException | RetryException executionException) {
            log.error(executionException.getMessage(), executionException);
        }

    }
}
