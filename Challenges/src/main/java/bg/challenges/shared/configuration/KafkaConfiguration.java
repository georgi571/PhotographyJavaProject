package bg.challenges.shared.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic topicBuilder() {
        return TopicBuilder.name("challenge-winners-event.v1").build();
    }
}
