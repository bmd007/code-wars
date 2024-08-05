package io.github.bmd007.codewars.game.engine;

import io.github.bmd007.codewars.game.engine.configuration.GameEngineProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaAdmin;

@ConfigurationPropertiesScan
@Slf4j
@SpringBootApplication
public class GameEngine {

    @Autowired
    private GameEngineProperties gameEngineProperties;
    @Autowired
    private KafkaAdmin kafkaAdmin;

    public static void main(String[] args) {
        SpringApplication.run(GameEngine.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        var gameEventsTopic = new NewTopic(gameEngineProperties.getGameEventsTopic(), 1, (short) 1);
        var playerACommandsTopic = new NewTopic(gameEngineProperties.getPlayerACommandsTopic(), 1, (short) 1);
        var playerBCommandsTopic = new NewTopic(gameEngineProperties.getPlayerBCommandsTopic(), 1, (short) 1);
        kafkaAdmin.createOrModifyTopics(gameEventsTopic, playerACommandsTopic, playerBCommandsTopic);
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {

    }
}
