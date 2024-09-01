package io.github.bmd007.codewars.game.client.dto;

import java.time.Instant;

public record GameCommand(String gameId, String tankId, String teamId, Action action, long timestamp) {
    public GameCommand(String gameId, String tankId, String teamId, Action action) {
        this(gameId, tankId, teamId, action, Instant.now().toEpochMilli());
    }
}
