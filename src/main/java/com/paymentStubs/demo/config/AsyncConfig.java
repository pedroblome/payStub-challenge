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
        executor.setCorePoolSize(5); // Inicia com 5 threads
        executor.setMaxPoolSize(10); // Pode crescer até 10 threads
        executor.setQueueCapacity(50); // Fila para 50 tarefas antes de criar mais threads
        executor.setThreadNamePrefix("CsvThread-");
        executor.initialize();
        return executor;
    }
}