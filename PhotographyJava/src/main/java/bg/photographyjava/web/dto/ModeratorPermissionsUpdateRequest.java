package bg.photographyjava.web.dto;

import bg.photographyjava.user.property.enums.UserPermission;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class ModeratorPermissionsUpdateRequest {

    @NotNull
    private Set<UserPermission> permissionsToAdd;

    @NotNull
    private Set<UserPermission> permissionsToRemove;

    @NotNull
    public Set<UserPermission> getPermissionsToAdd() {
        return permissionsToAdd;
    }

    public void setPermissionsToAdd(Set<UserPermission> permissionsToAdd) {
        this.permissionsToAdd = permissionsToAdd;
    }

    public Set<UserPermission> getPermissionsToRemove() {
        return permissionsToRemove;
    }

    public void setPermissionsToRemove(Set<UserPermission> permissionsToRemove) {
        this.permissionsToRemove = permissionsToRemove;
    }
}
