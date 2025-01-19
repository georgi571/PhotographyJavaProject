package bg.photographyjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PhotographyJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotographyJavaApplication.class, args);
    }

}
