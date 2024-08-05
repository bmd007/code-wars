package io.github.bmd007.codewars.game.engine.dto;

public record TankScored(String gameId, String tankId) implements GameEvent {
    public String key() {
        return tankId;
    }
}
