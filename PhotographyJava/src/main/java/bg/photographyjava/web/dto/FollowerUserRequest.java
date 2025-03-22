package bg.photographyjava.web.dto;

import jakarta.validation.constraints.NotNull;

public class FollowerUserRequest {
    @NotNull
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
