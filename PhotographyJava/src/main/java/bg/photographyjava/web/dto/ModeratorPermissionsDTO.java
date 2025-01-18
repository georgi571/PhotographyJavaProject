package bg.photographyjava.web.dto;

import bg.photographyjava.user.property.enums.UserPermission;

import java.util.Set;
import java.util.UUID;

public class ModeratorPermissionsDTO {

    private UUID id;

    private String username;

    private Set<UserPermission> permissions;

    public ModeratorPermissionsDTO() {
    }

    public ModeratorPermissionsDTO(UUID id, String username, Set<UserPermission> permissions) {
        this.id = id;
        this.username = username;
        this.permissions = permissions;
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

    public Set<UserPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }
}
