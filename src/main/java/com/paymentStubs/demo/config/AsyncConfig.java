package com.paymentStubs.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "csvTaskExecutor")
    public Executor csvTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // starts with 5 threads
        executor.setMaxPoolSize(10); // can grow to 10 threads
        executor.setQueueCapacity(50); // queue for 50 tasks before creating more threads
        executor.setThreadNamePrefix("CsvThread-");
        executor.initialize();
        return executor;
    }
}