package io.github.bmd007.codewars.game.leaderboard.serialization;

import io.github.bmd007.codewars.game.leaderboard.domain.Score;
import io.github.bmd007.codewars.game.leaderboard.dto.GameEvent;

public class CustomSerdes {
    public static final JsonSerde<GameEvent> GAME_EVENT_JSON_SERDE = new JsonSerde<>(GameEvent.class);
    public static final JsonSerde<Score> SCORE_JSON_SERDE = new JsonSerde<>(Score.class);
}
