package io.github.bmd007.codewars.game.engine.dto;

public record GameStartedEvent(String gameId, String teamAId, String teamBId) implements GameEvent {

}
