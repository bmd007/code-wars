package io.github.bmd007.codewars.game.leaderboard.service;

import io.github.bmd007.codewars.game.leaderboard.configuration.StateStores;
import io.github.bmd007.codewars.game.leaderboard.domain.Score;
import io.github.bmd007.codewars.game.leaderboard.dto.LeaderboardDto;
import io.github.bmd007.codewars.game.leaderboard.dto.ScoreDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class ScoresViewService extends ViewService<LeaderboardDto, ScoreDto, Score> {

    static final Function<LeaderboardDto, List<ScoreDto>> LIST_EXTRACTOR = leaderboardDto -> leaderboardDto.leaderboard().stream().sorted().toList();
    static final Function<List<ScoreDto>, LeaderboardDto> LIST_WRAPPER = scoreDtos -> new LeaderboardDto(scoreDtos.stream().sorted().toList());
    static final BiFunction<String, Score, ScoreDto> DTO_MAPPER = (_, score) -> new ScoreDto(score.team(), score.score());

    public ScoresViewService(StreamsBuilderFactoryBean streams,
                             @Value("${kafka.streams.server.config.app-ip}") String ip,
                             @Value("${kafka.streams.server.config.app-port}") int port,
                             ViewResourcesClient commonClient) {
        super(ip, port, streams, StateStores.SCORES_STATE_STORE,
                LeaderboardDto.class, ScoreDto.class,
                DTO_MAPPER, LIST_EXTRACTOR, LIST_WRAPPER, "/scores", commonClient);
    }
}
