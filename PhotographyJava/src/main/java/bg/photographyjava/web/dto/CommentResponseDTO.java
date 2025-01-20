package bg.photographyjava.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentResponseDTO {
    private UUID id;

    private String text;

    private UserInformationForPictureDTO author;

    private LocalDateTime dateTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UserInformationForPictureDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserInformationForPictureDTO author) {
        this.author = author;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
