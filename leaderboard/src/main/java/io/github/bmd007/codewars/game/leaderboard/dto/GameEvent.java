package io.github.bmd007.codewars.game.leaderboard.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GameStartedEvent.class, name = "gameStartedEvent"),
        @JsonSubTypes.Type(value = TankScored.class, name = "tankScored")
})
public interface GameEvent {
    String gameId();

    default String id() {
        return UUID.randomUUID().toString();
    }

    default Instant eventTime() {
        return Instant.now();
    }

    default String key() {
        return gameId();
    }
}
