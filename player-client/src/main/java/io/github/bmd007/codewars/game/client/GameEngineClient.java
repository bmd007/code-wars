package io.github.bmd007.codewars.game.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GameEngineClient {

    private final WebClient gameEngineWebClient;

    public GameEngineClient(GameClientProperties gameClientProperties) {
        this.gameEngineWebClient = WebClient.builder()
                .baseUrl(gameClientProperties.getGameEngineHost().toString())
                .build();
    }

    public Mono<GameState> getGameState() {
        return gameEngineWebClient.get()
                .uri("/games/current")
                .retrieve()
                .bodyToMono(GameState.class);
    }
}
