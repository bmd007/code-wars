package io.github.bmd007.codewars.game.leaderboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class Leaderboard {

	public static void main(String[] args) {
		SpringApplication.run(Leaderboard.class, args);
	}

}
