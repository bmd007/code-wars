package io.github.bmd007.codewars.game.engine.service;

import io.github.bmd007.codewars.game.engine.configuration.GameEngineProperties;
import io.github.bmd007.codewars.game.engine.domain.Game;
import io.github.bmd007.codewars.game.engine.dto.GameEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
public class GameService {
    private final ConcurrentLinkedQueue<Game.Action> tankAActions = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Game.Action> tankBActions = new ConcurrentLinkedQueue<>();
    private final Random random = new Random();

    private final KafkaTemplate<String, GameEvent> kafkaTemplate;
    private final GameEngineProperties gameProperties;
    private Game game;

    public GameService(KafkaTemplate<String, GameEvent> kafkaTemplate, GameEngineProperties gameProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.gameProperties = gameProperties;
        this.game = createGame();
    }

    private Game roundOne() {
        return new Game(gameProperties.getGameId(), gameProperties.getPlayerAId(), gameProperties.getPlayerBId());
    }

    public Game getGame() {
        return game;
    }

    private Game roundTwo() {
        return new Game(gameProperties.getGameId(), gameProperties.getPlayerBId(), gameProperties.getPlayerAId());
    }

    public void endGame() {
        tankAActions.clear();
        tankBActions.clear();
    }

    public void queueActionForTankA(Game.Action action) {
        tankAActions.add(action);
    }

    public void queueActionForTankB(Game.Action action) {
        tankBActions.add(action);
    }

    //todo add retry logic (for 300 milliseconds)
    private void applyActionToTankAIfAny() {
        Optional.ofNullable(tankAActions.poll()).ifPresent(game::applyActionToTankA);
    }

    //todo add retry logic (for 300 milliseconds)
    private void applyActionToTankBIfAny() {
        Optional.ofNullable(tankBActions.poll()).ifPresent(game::applyActionToTankB);
    }

    public Game createGame() {
        this.game = roundOne();
        //todo define a good interval
        Flux.interval(Duration.ofMillis(50))
                .filter(_ -> game != null)
                .subscribe(_ -> {
                    applyActionToTankAIfAny();
                    applyActionToTankBIfAny();
                });
        Flux.interval(Duration.ofMillis(100))
                .filter(_ -> game != null)
                .filter(_ -> !game.isOver())
                .flatMap(_ -> Flux.fromIterable(game.getLatestGameEvents()))
                .flatMap(this::publishGameEvent)
                .subscribe();
        return game;
    }

    public Mono<Void> publishGameEvent(GameEvent gameEvent) {
        return Mono.fromFuture(kafkaTemplate.send(gameProperties.getGameEventsTopic(), gameEvent.key(), gameEvent))
                .doOnNext(_ -> log.info("Game event published: {}", gameEvent))
                .then();
    }
}
