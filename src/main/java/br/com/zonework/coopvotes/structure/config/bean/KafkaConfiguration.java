package br.com.zonework.coopvotes.structure.config.bean;

import br.com.zonework.coopvotes.core.notification.model.MessageNotification;
import br.com.zonework.coopvotes.structure.config.property.KafkaProducerProperties;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;
    private final KafkaProducerProperties kafkaProducerProperties;

    @Bean
    public Map<String, Object> producerConfig() {
        var props = new HashMap<>(Map.<String, Object>copyOf(kafkaProperties.getProperties()));

        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafkaProducerProperties.getBootstrapServersHost());

        return props;
    }

    @Bean
    public ProducerFactory<String, MessageNotification> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, MessageNotification> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopics topicStartConnection(KafkaProducerProperties properties) {
        return new NewTopics(TopicBuilder.name(properties.getTopic()).build());
    }
}
