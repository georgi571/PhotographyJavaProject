package bg.leaderboards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeaderboardsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeaderboardsApplication.class, args);
	}

}
