package io.github.bmd007.codewars.game.engine.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "game.engine")
@Getter
@Builder
@ToString
public class GameEngineProperties {
    String gameId;
    String playerAId;
    String playerBId;
    String gameEventsTopic;
    String playerACommandsTopic;
    String playerBCommandsTopic;
}
