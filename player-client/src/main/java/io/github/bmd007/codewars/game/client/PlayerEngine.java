package io.github.bmd007.codewars.game.client;

import io.github.bmd007.codewars.game.client.client.GameEngineClient;
import io.github.bmd007.codewars.game.client.domain.GameState;
import io.github.bmd007.codewars.game.client.dto.GameCommand;
import io.github.bmd007.codewars.game.client.properties.GameClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
public class PlayerEngine {

    private final GameClientProperties gameClientProperties;
    private final KafkaTemplate<String, GameCommand> kafkaTemplate;
    private final GameEngineClient gameEngineClient;

    public PlayerEngine(GameClientProperties gameClientProperties,
                        KafkaTemplate<String, GameCommand> kafkaTemplate,
                        GameEngineClient gameEngineClient) {
        this.gameClientProperties = gameClientProperties;
        this.kafkaTemplate = kafkaTemplate;
        this.gameEngineClient = gameEngineClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    private void play() {
        var baseCommand = new GameCommand(gameClientProperties.getGameId(), gameClientProperties.getPlayerId(), gameClientProperties.getTeamId(), null);
        Flux.interval(Duration.ofMillis(500))
                .flatMap(_ -> gameEngineClient.getGameState())
                .map(gameState -> {
                    gameState.setMyTankId(gameClientProperties.getPlayerId());
                    return gameState;
                })
                .map(GameState::decideNextAction)
                .map(baseCommand::withAction)
                .flatMap(data -> Mono.fromFuture(kafkaTemplate.send(gameClientProperties.getGameCommandsTopic(), data)))
                .switchIfEmpty(Mono.error(new Throwable("Empty")))
                .onErrorContinue((throwable, _) -> log.error("Error getting/sending game state", throwable))
                .subscribe();
    }
}
