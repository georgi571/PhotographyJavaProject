package bg.photographyjava.web.dto;

import java.util.UUID;

public class ApproveUsersResponse {
    private UUID id;

    private String username;

    private String email;

    public ApproveUsersResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
