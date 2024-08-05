package io.github.bmd007.codewars.game.client;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "game.client")
@Getter
@Builder
@ToString
public class GameClientProperties {
    String teamId;
    String gameId;
    String playerId;
    String gameEventsTopic;
    String gameCommandsTopic;
    URI gameEngineHost;
}
