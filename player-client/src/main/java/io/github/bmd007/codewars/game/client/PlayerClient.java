package io.github.bmd007.codewars.game.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@ConfigurationPropertiesScan
@Slf4j
@SpringBootApplication
public class PlayerClient {

    public static void main(String[] args) {
        SpringApplication.run(PlayerClient.class, args);
    }

    @Autowired
    private GameClientProperties gameClientProperties;

    @Autowired
    private KafkaTemplate<String, GameCommand> kafkaTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        log.info("Starting game client with properties: {}", gameClientProperties);
        Flux.fromIterable(
                List.of(
                        new GameCommand(gameClientProperties.gameId, gameClientProperties.playerId, gameClientProperties.teamId, GameCommand.Action.LOOK_DOWN),
                        new GameCommand(gameClientProperties.gameId, gameClientProperties.playerId, gameClientProperties.teamId, GameCommand.Action.FIRE),
                        new GameCommand(gameClientProperties.gameId, gameClientProperties.playerId, gameClientProperties.teamId, GameCommand.Action.LOOK_RIGHT),
                        new GameCommand(gameClientProperties.gameId, gameClientProperties.playerId, gameClientProperties.teamId, GameCommand.Action.FIRE),
                        new GameCommand(gameClientProperties.gameId, gameClientProperties.playerId, gameClientProperties.teamId, GameCommand.Action.LOOK_UP),
                        new GameCommand(gameClientProperties.gameId, gameClientProperties.playerId, gameClientProperties.teamId, GameCommand.Action.FIRE),
                        new GameCommand(gameClientProperties.gameId, gameClientProperties.playerId, gameClientProperties.teamId, GameCommand.Action.LOOK_LEFT),
                        new GameCommand(gameClientProperties.gameId, gameClientProperties.playerId, gameClientProperties.teamId, GameCommand.Action.FIRE),
                        new GameCommand(gameClientProperties.gameId, gameClientProperties.playerId, gameClientProperties.teamId, GameCommand.Action.LOOK_DOWN),
                        new GameCommand(gameClientProperties.gameId, gameClientProperties.playerId, gameClientProperties.teamId, GameCommand.Action.FIRE)
                )
        )
        .delayElements(Duration.ofMillis(400))
        .repeat(300)
        .flatMap(data ->Mono.fromFuture(kafkaTemplate.send(gameClientProperties.gameCommandsTopic, data)))
        .subscribe(System.out::println);
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {

    }
}
