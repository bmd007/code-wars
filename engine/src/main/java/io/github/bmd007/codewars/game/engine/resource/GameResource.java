package io.github.bmd007.codewars.game.engine.resource;

import io.github.bmd007.codewars.game.engine.configuration.GameEngineProperties;
import io.github.bmd007.codewars.game.engine.domain.Game;
import io.github.bmd007.codewars.game.engine.dto.GameCommand;
import io.github.bmd007.codewars.game.engine.repository.CommandRepository;
import io.github.bmd007.codewars.game.engine.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/games")
public class GameResource {

    private final CommandRepository commandRepository;
    private final GameEngineProperties gameEngineProperties;
    private final GameService gameService;

    public GameResource(CommandRepository commandRepository,
                        GameEngineProperties gameEngineProperties,
                        GameService gameService) {
        this.commandRepository = commandRepository;
        this.gameEngineProperties = gameEngineProperties;
        this.gameService = gameService;
    }

    @PostMapping
    public Game createGame() {
        return gameService.createGame();
    }

    @GetMapping("/current")
    public Game getGame() {
        return gameService.getGame();
    }

    @GetMapping("/actions")
    public Set<Game.Action> getCommands() {
        return Set.of(Game.Action.values());
    }

    @PutMapping("/current/commands")
    public void sendCommand(@RequestBody GameCommand command) {
        commandRepository.saveGameCommand(command);
        if (gameEngineProperties.getPlayerAId().equals(command.tankId())) {
            gameService.queueActionForTankA(command.action());
        } else if (gameEngineProperties.getPlayerBId().equals(command.tankId())) {
            gameService.queueActionForTankB(command.action());
        } else {
            throw new IllegalArgumentException("Invalid tankId: " + command.tankId());
        }
    }

    @DeleteMapping("/current")
    public void deleteGame() {
        gameService.endGame();
    }
}
