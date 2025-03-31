package bg.photographyjava.web.dto;

import jakarta.validation.constraints.NotNull;

public class RoleRequest {

    @NotNull
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
