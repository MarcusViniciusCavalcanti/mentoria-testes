package br.com.zonework.coopvotes;

import br.com.zonework.coopvotes.structure.config.property.KafkaProducerProperties;
import br.com.zonework.coopvotes.structure.config.property.RedisProperty;
import br.com.zonework.coopvotes.structure.config.property.ThreadProperty;
import com.github.sonus21.rqueue.spring.EnableRqueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableRqueue
@EnableFeignClients
@EnableConfigurationProperties(value = {
    RedisProperty.class,
    KafkaProducerProperties.class,
    ThreadProperty.class
})
public class CoopVotesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoopVotesApplication.class, args);
    }

}
