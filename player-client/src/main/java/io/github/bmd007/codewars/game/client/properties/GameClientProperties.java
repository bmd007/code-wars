package io.github.bmd007.codewars.game.client.properties;

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
    private String teamId;
    private String gameId;
    private String playerId;
    private String gameEventsTopic;
    private String gameCommandsTopic;
    private URI gameEngineHost;
}
