package bg.photographyjava.user.property.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserPermission {
    APPROVE_USERS("approveUsers"),
    CHANGE_USER_ROLES("changeUserRoles"),
    BAN_USERS("banUsers"),
    ANSWER_FEEDBACK("answerFeedback"),
    DELETE_MESSAGE("deleteMessage"),
    DELETE_PICTURE("deletePicture"),
    MANAGE_CHALLENGE("manageChallenge");

    private final String permissionName;

    UserPermission(String permissionName) {
        this.permissionName = permissionName;
    }

    @JsonValue
    public String getPermissionName() {
        return permissionName;
    }

    @JsonCreator
    public static UserPermission fromString(String value) {
        for (UserPermission permission : UserPermission.values()) {
            if (permission.permissionName.equalsIgnoreCase(value)) {
                return permission;
            }
        }
        throw new IllegalArgumentException("Unknown permission: " + value);
    }
}
