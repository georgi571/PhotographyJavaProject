package bg.photographyjava.web.dto;

import bg.photographyjava.user.property.enums.UserPermission;

import java.util.Set;

public class AdminPermissionsUpdateRequest {

    private Set<UserPermission> permissionsToAdd;

    private Set<UserPermission> permissionsToRemove;

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
