package com.paymentStubs.demo.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AsyncConfig.class)
class AsyncConfigTest {

    @Autowired
    @Qualifier("csvTaskExecutor")
    private Executor taskExecutor;

    @Test
    @DisplayName("Should load the csvTaskExecutor bean into the context")
    void csvTaskExecutor_ShouldBeLoaded() {
        assertNotNull(taskExecutor, "The csvTaskExecutor bean should not be null.");
    }

    @Test
    @DisplayName("Should be an instance of ThreadPoolTaskExecutor")
    void csvTaskExecutor_ShouldBeCorrectType() {
        assertTrue(taskExecutor instanceof ThreadPoolTaskExecutor,
                "The bean should be an instance of ThreadPoolTaskExecutor.");
    }

    @Test
    @DisplayName("Should have the correct properties configured")
    void csvTaskExecutor_ShouldHaveCorrectProperties() {
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) taskExecutor;
        assertEquals(5, executor.getCorePoolSize(), "Core pool size should be configured to 5.");
        assertEquals(10, executor.getMaxPoolSize(), "Max pool size should be configured to 10.");
        assertEquals(50, executor.getQueueCapacity(), "Queue capacity should be configured to 50.");
        assertTrue(executor.getThreadNamePrefix().startsWith("CsvThread-"),
                "Thread name prefix should be correctly set.");
    }
}
