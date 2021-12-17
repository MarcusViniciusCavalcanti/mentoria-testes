package br.com.zonework.coopvotes.structure.config.bean;

import br.com.zonework.coopvotes.structure.config.property.ThreadProperty;
import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class ThreadPoolExecutorContextConfiguration {

    @Bean
    public Executor taskExecutor(ThreadProperty property) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(property.getCore());
        executor.setMaxPoolSize(property.getPoolSize());
        executor.setQueueCapacity(property.getQueueCapacity());
        executor.setThreadNamePrefix(property.getPrefix());

        return executor;
    }

}
