package io.github.bmd007.codewars.game.engine.dto;

import io.github.bmd007.codewars.game.engine.domain.Game;

import java.time.Instant;

public record GameCommand(String gameId, String tankId, String teamId, Game.Action action, long timestamp) {
    public GameCommand(String gameId, String tankId, String teamId, Game.Action action) {
        this(gameId, tankId, teamId, action, Instant.now().toEpochMilli());
    }
}
