package io.github.bmd007.codewars.game.client;

import java.time.Instant;

public record GameCommand(String gameId, String tankId, String teamId, Action action, long timestamp) {
    public GameCommand(String gameId, String tankId, String teamId, Action action) {
        this(gameId, tankId, teamId, action, Instant.now().toEpochMilli());
    }
    public enum Action {
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
        LOOK_UP,
        LOOK_DOWN,
        LOOK_LEFT,
        LOOK_RIGHT,
        FIRE
    }
}
