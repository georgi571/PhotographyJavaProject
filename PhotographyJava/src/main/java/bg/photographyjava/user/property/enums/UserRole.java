package bg.photographyjava.user.property.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum UserRole {
    ADMIN, MODERATOR, USER;

    public static List<UserRole> getRoles() {
        return new ArrayList<>(Arrays.asList(UserRole.values()));
    }
}
