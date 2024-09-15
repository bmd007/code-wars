package io.github.bmd007.codewars.game.engine;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "messageEvents",
//        Stores.MOVER_IN_MEMORY_STATE_STORE + "-" + "stateful-geofencing-faas-changelog",
//        "event_log"
})
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
