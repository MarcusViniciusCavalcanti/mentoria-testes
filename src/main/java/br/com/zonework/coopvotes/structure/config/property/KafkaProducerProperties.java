package br.com.zonework.coopvotes.structure.config.property;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("coopvotes.configurations.kafka.producer")
public class KafkaProducerProperties {

    @NotBlank(message = "bootstrap server Kafka host is undefined, please configure host Kafka")
    private String bootstrapServersHost;

    @NotBlank(message = "Kafka topic start connection is undefined, please add at least one topic")
    private String topic;
}
