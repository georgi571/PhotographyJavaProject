package bg.photographyjava.web.dto;

import jakarta.validation.constraints.NotNull;

public class FriendRequest {

    @NotNull
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
