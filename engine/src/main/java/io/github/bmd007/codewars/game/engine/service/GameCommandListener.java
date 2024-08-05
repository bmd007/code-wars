package io.github.bmd007.codewars.game.engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bmd007.codewars.game.engine.repository.CommandRepository;
import io.github.bmd007.codewars.game.engine.configuration.GameEngineProperties;
import io.github.bmd007.codewars.game.engine.dto.GameCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.HashMap;
import java.util.Set;

@Slf4j
@Component
public class GameCommandListener {
    private final GameEngineProperties gameEngineProperties;
    private final KafkaProperties kafkaProperties;
    private final GameService gameService;
    private final CommandRepository commandRepository;
    private final ObjectMapper objectMapper;

    public GameCommandListener(GameEngineProperties gameEngineProperties,
                               KafkaProperties kafkaProperties,
                               GameService gameService,
                               CommandRepository commandRepository,
                               ObjectMapper objectMapper) {
        this.gameEngineProperties = gameEngineProperties;
        this.kafkaProperties = kafkaProperties;
        this.gameService = gameService;
        this.commandRepository = commandRepository;
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        var consumerProps = new HashMap<String, Object>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "engine-%s".formatted(gameEngineProperties.getGameId()));
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        var tankACommandsReceiverOptions = ReceiverOptions.<String, String>create(consumerProps)
                .subscription(Set.of(gameEngineProperties.getPlayerACommandsTopic()));
        KafkaReceiver.create(tankACommandsReceiverOptions)
                .receive()
                .subscribe(message -> {
                    try {
                        GameCommand command = objectMapper.readValue(message.value(), GameCommand.class);
                        log.info("Received command for tank A: {}", command);
                        commandRepository.saveGameCommand(command);
                        if (!command.tankId().equals(gameEngineProperties.getPlayerAId())) {
                            log.warn("Received command for wrong tank: {}", command);
                            return;
                        }
                        gameService.queueActionForTankA(command.action());
                        message.receiverOffset().acknowledge();
                    } catch (JsonProcessingException e) {
                       log.error("Error processing command", e);
                    }
                });

        var tankBCommandsReceiverOptions = ReceiverOptions.<String, String>create(consumerProps)
                .subscription(Set.of(gameEngineProperties.getPlayerBCommandsTopic()));
        KafkaReceiver.create(tankBCommandsReceiverOptions)
                .receive()
                .subscribe(message -> {
                    try {
                        GameCommand command = objectMapper.readValue(message.value(), GameCommand.class);
                        log.info("Received command for tank B: {}", command);
                        commandRepository.saveGameCommand(command);
                        if (!command.tankId().equals(gameEngineProperties.getPlayerBId())) {
                            log.warn("Received command for wrong tank: {}", command);
                            return;
                        }
                        gameService.queueActionForTankB(command.action());
                        message.receiverOffset().acknowledge();
                    } catch (JsonProcessingException e) {
                       log.error("Error processing command", e);
                    }
                });
    }
}
