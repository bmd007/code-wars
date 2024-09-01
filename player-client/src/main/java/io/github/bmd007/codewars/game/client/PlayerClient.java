package io.github.bmd007.codewars.game.client;

import io.github.bmd007.codewars.game.client.client.GameEngineClient;
import io.github.bmd007.codewars.game.client.dto.Action;
import io.github.bmd007.codewars.game.client.dto.GameCommand;
import io.github.bmd007.codewars.game.client.properties.GameClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

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

    @Autowired
    private GameEngineClient gameEngineClient;

//    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        Random random = new Random();
        List<GameCommand> commands = List.of(
                new GameCommand(gameClientProperties.getGameId(), gameClientProperties.getPlayerId(), gameClientProperties.getTeamId(), Action.FIRE),
                new GameCommand(gameClientProperties.getGameId(), gameClientProperties.getPlayerId(), gameClientProperties.getTeamId(), Action.MOVE_LEFT),
                new GameCommand(gameClientProperties.getGameId(), gameClientProperties.getPlayerId(), gameClientProperties.getTeamId(), Action.MOVE_RIGHT),
                new GameCommand(gameClientProperties.getGameId(), gameClientProperties.getPlayerId(), gameClientProperties.getTeamId(), Action.MOVE_UP),
                new GameCommand(gameClientProperties.getGameId(), gameClientProperties.getPlayerId(), gameClientProperties.getTeamId(), Action.MOVE_DOWN)
        );
        Flux.interval(Duration.ofMillis(500))
        .map(_ -> random.nextInt(commands.size()))
        .map(commands::get)
        .flatMap(data ->Mono.fromFuture(kafkaTemplate.send(gameClientProperties.getGameCommandsTopic(), data)))
        .subscribe(System.out::println);
        Flux.interval(Duration.ofMillis(500))
                .flatMap(_ -> gameEngineClient.getGameState())
                .onErrorContinue((throwable, _) -> log.error("Error getting game state", throwable))
                .subscribe(gameState -> log.info("Received game state: {}", gameState));
    }
}
