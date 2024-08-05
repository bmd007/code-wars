package io.github.bmd007.codewars.game.leaderboard.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.regex.Pattern;

import static org.apache.kafka.common.config.TopicConfig.CLEANUP_POLICY_COMPACT;
import static org.apache.kafka.common.config.TopicConfig.CLEANUP_POLICY_CONFIG;
import static org.apache.kafka.common.config.TopicConfig.RETENTION_BYTES_CONFIG;
import static org.apache.kafka.common.config.TopicConfig.RETENTION_MS_CONFIG;

@Configuration
@Profile("!test")
public class TopicCreator {

    private final PartitionDef eventTopicDefinition;
    private final PartitionDef changeLogTopicDefinition;
    private final String applicationName;

    public TopicCreator(
            @Value("${spring.application.name}") String applicationName,
            @Value("${kafka.topic.config.event}") String eventTopicDefinition,
            @Value("${kafka.topic.config.changelog}") String changeLogTopicDefinition) {
        this.applicationName = applicationName;
        this.eventTopicDefinition = PartitionDef.parse(eventTopicDefinition);
        this.changeLogTopicDefinition = PartitionDef.parse(changeLogTopicDefinition);

    }

    public static String stateStoreTopicName(String storeName, String applicationName) {
        return String.format("%s-%s-changelog", applicationName, storeName);
    }

    @Bean
    public NewTopic scoresChangeLogTopic() {
        return new NewTopic(stateStoreTopicName(StateStores.SCORES_STATE_STORE, applicationName),
                changeLogTopicDefinition.numPartitions, changeLogTopicDefinition.replicationFactor)
                .configs(Map.of(CLEANUP_POLICY_CONFIG, CLEANUP_POLICY_COMPACT));
    }

    private record PartitionDef(int numPartitions, short replicationFactor) {

        private static final Pattern PATTERN = Pattern.compile("(\\d+):(\\d+)");

        public static PartitionDef parse(String value) {
            var matcher = PATTERN.matcher(value);
            if (matcher.matches()) {
                var numParts = Integer.parseInt(matcher.group(1));
                var repFactor = Short.parseShort(matcher.group(2));
                return new PartitionDef(numParts, repFactor);
            } else {
                throw new IllegalArgumentException("Invalid topic partition definition: " + value);
            }
        }
    }
}
