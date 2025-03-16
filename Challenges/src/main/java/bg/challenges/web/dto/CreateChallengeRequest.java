package bg.challenges.web.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class CreateChallengeRequest implements Serializable {
    private String title;
    private String description;
    private String details;
    private LocalDate startAt;
    private LocalDate endAt;
    private String type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDate getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDate startAt) {
        this.startAt = startAt;
    }

    public LocalDate getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDate endAt) {
        this.endAt = endAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
