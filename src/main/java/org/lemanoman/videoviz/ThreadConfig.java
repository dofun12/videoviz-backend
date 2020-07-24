package org.lemanoman.videoviz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadConfig {

    @Bean("downloadTask")
    public TaskExecutor downloadTask() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setQueueCapacity(300);
        executor.setMaxPoolSize(4);
        executor.setKeepAliveSeconds(3600);
        executor.setThreadNamePrefix("downloadTask-");
        executor.initialize();
        return executor;
    } 

    /**
    @Bean
    public TaskExecutor executorB() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("executor-B");
        executor.initialize();
        return executor;
    }**/
}  