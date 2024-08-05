package io.github.bmd007.codewars.game.leaderboard.resource;

import io.github.bmd007.codewars.game.leaderboard.dto.LeaderboardDto;
import io.github.bmd007.codewars.game.leaderboard.dto.ScoreDto;
import io.github.bmd007.codewars.game.leaderboard.exception.NotFoundException;
import io.github.bmd007.codewars.game.leaderboard.service.ScoresViewService;
import io.github.bmd007.codewars.game.leaderboard.service.ViewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/leaderboards")
public class ScoresResource {

    private final ScoresViewService scoresViewService;

    public ScoresResource(ScoresViewService scoresViewService) {
        this.scoresViewService = scoresViewService;
    }

    //isHighLevelQuery query param is related to inter instance communication,
    // and it should be true in normal operations or not defined
    @GetMapping
    public Mono<LeaderboardDto> getScores(@RequestParam(required = false,
            value = ViewService.HIGH_LEVEL_QUERY_PARAM_NAME, defaultValue = "true") boolean isHighLevelQuery) {
        return scoresViewService.getAll(isHighLevelQuery);
    }

    @GetMapping("/{team}")
    public Mono<ScoreDto> getCounterByName(@PathVariable String team) {
        return scoresViewService.getById(team)
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("%s not found (%s doesn't exist).", "scores", team))));
    }
}
