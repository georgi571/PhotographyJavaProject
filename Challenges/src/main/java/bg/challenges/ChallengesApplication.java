package bg.challenges;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChallengesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChallengesApplication.class, args);
	}

}
