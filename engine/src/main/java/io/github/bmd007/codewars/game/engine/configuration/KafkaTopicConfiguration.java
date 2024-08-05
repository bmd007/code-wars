package io.github.bmd007.codewars.game.engine.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Slf4j
@Configuration
public class KafkaTopicConfiguration {

    private final KafkaProperties kafkaProperties;

    public KafkaTopicConfiguration(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        log.info("Creating KafkaAdmin bean with properties: {}", kafkaProperties);
        KafkaAdmin kafkaAdmin = new KafkaAdmin(kafkaProperties.buildAdminProperties(null));
        kafkaAdmin.setFatalIfBrokerNotAvailable(kafkaProperties.getAdmin().isFailFast());
        return kafkaAdmin;
    }
}