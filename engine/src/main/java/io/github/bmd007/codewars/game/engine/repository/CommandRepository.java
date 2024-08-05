package io.github.bmd007.codewars.game.engine.repository;

import io.github.bmd007.codewars.game.engine.dto.GameCommand;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CommandRepository {
    public void saveGameCommand(GameCommand gameCommand) {
        // save game command
        Mono.justOrEmpty(gameCommand).subscribe();
    }
}
