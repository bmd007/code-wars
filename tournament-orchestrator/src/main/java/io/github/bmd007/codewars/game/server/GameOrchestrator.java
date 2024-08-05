package io.github.bmd007.codewars.game.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@SpringBootApplication
public class GameOrchestrator {

    public static void main(String[] args) {
        SpringApplication.run(GameOrchestrator.class, args);
    }

    @Autowired
    private ResourceLoader resourceLoader;

    private final Map<String, String> playerIdTeamCombinations = new HashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void start() throws IOException {
        String gameId = UUID.randomUUID().toString();
        String playerAId = addNewPlayer("A");
        String playerBId = addNewPlayer("B");

        var templateFile = resourceLoader.getResource("classpath:tournament-orchestrator-compose.yml.template").getFile();
        String orchestrationComposeContent = new String(Files.readAllBytes(templateFile.toPath()));
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("game_id", gameId);
        valuesMap.put("player_a_id", playerAId);
        valuesMap.put("player_b_id", playerBId);
        valuesMap.put("player_a_command_topic", playerAId + "-commands");
        valuesMap.put("player_b_command_topic", playerBId + "-commands");
//        valuesMap.put("game_events_topic", gameId + "-events");
        valuesMap.put("game_events_topic", "game-events");
        valuesMap.put("player_a_image", "bmd007/codewars-player");
        valuesMap.put("player_b_image", "bmd007/codewars-player");
        for (Map.Entry<String, String> entry : valuesMap.entrySet()) {
            orchestrationComposeContent = orchestrationComposeContent.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        var orchestrationComposePath = Path.of("game-orchestra-compose.yml");
        Files.write(orchestrationComposePath, orchestrationComposeContent.getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String addNewPlayer(String team) {
        String playerId = UUID.randomUUID().toString();
        playerIdTeamCombinations.put(playerId, team);
        return playerId;
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {

    }
}
