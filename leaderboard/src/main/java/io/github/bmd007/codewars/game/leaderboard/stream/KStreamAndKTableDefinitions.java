package io.github.bmd007.codewars.game.leaderboard.stream;

import io.github.bmd007.codewars.game.leaderboard.configuration.LeaderboardProperties;
import io.github.bmd007.codewars.game.leaderboard.domain.Score;
import io.github.bmd007.codewars.game.leaderboard.dto.TankScored;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Configuration;

import static io.github.bmd007.codewars.game.leaderboard.configuration.StateStores.SCORES_STATE_STORE;
import static io.github.bmd007.codewars.game.leaderboard.serialization.CustomSerdes.GAME_EVENT_JSON_SERDE;
import static io.github.bmd007.codewars.game.leaderboard.serialization.CustomSerdes.SCORE_JSON_SERDE;

@Slf4j
@Configuration
public class KStreamAndKTableDefinitions {

    private final StreamsBuilder builder;
    private final LeaderboardProperties leaderboardProperties;

    public KStreamAndKTableDefinitions(StreamsBuilder builder, LeaderboardProperties leaderboardProperties) {
        this.builder = builder;
        this.leaderboardProperties = leaderboardProperties;
    }

    @PostConstruct
    public void configureStores() {
        builder.stream(leaderboardProperties.getGameEventsTopic(), Consumed.with(Serdes.String(), GAME_EVENT_JSON_SERDE))
                .filterNot((k, v) -> k == null || k.isBlank() || k.isEmpty() || v == null || v.key() == null || v.key().isEmpty() || v.key().isBlank())
                .filter((k, v) -> k.equals(v.key()))
                .filter((_, v) -> v instanceof TankScored)
                .groupByKey()
                .aggregate(
                        () -> new Score("empty", 0),
                        (key, event, currentScore) ->
                                switch (event) {
                                    case TankScored _ -> {
                                        if (!currentScore.team().equals(key)) {
                                            // first score for this team
                                            yield new Score(key, 1);
                                        } else {
                                            yield currentScore.addScore();
                                        }
                                    }
                                    case null, default -> new Score(key);
                                },
                        Materialized.<String, Score>as(Stores.persistentKeyValueStore(SCORES_STATE_STORE))
                                .withKeySerde(Serdes.String())
                                .withValueSerde(SCORE_JSON_SERDE)
                );
        //todo create a global store for the leaderboard with a custom SynchronizedSortedMap as internal implementation of store
    }
}
