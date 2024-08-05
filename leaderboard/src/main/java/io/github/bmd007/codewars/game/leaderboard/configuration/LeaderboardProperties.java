package io.github.bmd007.codewars.game.leaderboard.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "game.leaderboard")
@Getter
@Builder
@ToString
public class LeaderboardProperties {
    String gameEventsTopic;
    URI gameEngineHost;
}
