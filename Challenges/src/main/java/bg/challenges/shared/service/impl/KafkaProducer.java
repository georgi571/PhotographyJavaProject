package bg.challenges.shared.service.impl;

import bg.challenges.web.dto.WinnerRegisterV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, WinnerRegisterV1> kafkaTemplate;

    public void sendMessage(WinnerRegisterV1 event) {
        kafkaTemplate.send("challenge-winners-event.v1", event);
    }
}
