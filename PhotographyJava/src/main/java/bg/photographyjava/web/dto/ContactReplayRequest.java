package bg.photographyjava.web.dto;

import java.util.UUID;

public class ContactReplayRequest {
    private UUID id;

    private String answer;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
