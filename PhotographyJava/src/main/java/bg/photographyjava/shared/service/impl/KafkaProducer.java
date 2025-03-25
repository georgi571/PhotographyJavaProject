package bg.photographyjava.shared.service.impl;

import bg.photographyjava.web.dto.UserRegisterV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, UserRegisterV1> kafkaTemplate;

    public void sendMessage(UserRegisterV1 event) {
        kafkaTemplate.send("user-registered-event.v1", event);
    }
}
