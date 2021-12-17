package br.com.zonework.coopvotes.core.notification.service;

import br.com.zonework.coopvotes.core.notification.model.MessageNotification;
import br.com.zonework.coopvotes.structure.config.property.KafkaProducerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderNotification {

    private final KafkaProducerProperties kafkaProducerProperties;
    private final KafkaTemplate<String, MessageNotification> template;

    public void sendNotification(MessageNotification messageNotification) {
        var topic = kafkaProducerProperties.getTopic();
        log.info("Sending message to topic {}", topic);
        log.debug("With payload {}", messageNotification);
        template.send(topic, messageNotification);
    }
}
