package bg.photographyjava.web.dto;

import java.util.UUID;

public class ChangeRoleUserResponse {

    private UUID id;

    private String username;

    private String role;

    public ChangeRoleUserResponse(UUID id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
