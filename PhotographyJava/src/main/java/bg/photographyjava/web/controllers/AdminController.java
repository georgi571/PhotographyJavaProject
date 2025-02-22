package bg.photographyjava.web.controllers;

import bg.photographyjava.user.property.enums.UserPermission;
import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/change-roles")
    public ResponseEntity<List<ChangeRoleUserResponse>> getAllUsers() {

        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    @PutMapping("/change-roles/{id}")
    public ResponseEntity<Map<String, String>> updateUserRole(
            @PathVariable UUID id,
            @RequestBody RoleRequest roleRequest,
            Authentication authentication) {

        this.userService.updateUserRole(id, roleRequest.getRole(), authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Role updated successfully"));
    }

    @GetMapping("/ban-users")
    public ResponseEntity<List<BanUserResponse>> getUsersForBan() {

        return ResponseEntity.ok(this.userService.getAllUsersForBan());
    }


    @PutMapping("/ban-users/{id}")
    public ResponseEntity<BanUserResponse> banOrUnbanUser(
            @PathVariable UUID id,
            @RequestBody BanUserReasonRequest request,
            Authentication authentication) {

        this.userService.banUserAction(id, request, authentication.getName());
        return ResponseEntity.ok(this.userService.getUserForBan(id));
    }

    @GetMapping("/approve-users")
    public ResponseEntity<List<ApproveUsersResponse>> getUsersForApprove() {

        return ResponseEntity.ok(this.userService.getAllUsersForApprove());
    }

    @PutMapping("/approve-users/{id}")
    public ResponseEntity<ApproveUsersResponse> approveUser(
            @PathVariable UUID id,
            @RequestBody ApproveUserReasonRequest request,
            Authentication authentication) {

        this.userService.approveUserAction(id, request, authentication.getName());
        return ResponseEntity.ok(this.userService.getUserForApprove(id));
    }

    @GetMapping("/admin-permissions")
    public ResponseEntity<List<AdminPermissionsResponse>> getAdminsWithPermissions() {

        return ResponseEntity.ok(this.userService.getAllAdminsWithPermissions());
    }

    @PutMapping("/admin-permissions/{id}")
    public ResponseEntity<AdminPermissionsResponse> updateAdminPermissions(
            @PathVariable UUID id,
            @RequestBody AdminPermissionsUpdateRequest updatePermission,
            Authentication authentication) {

        return ResponseEntity.ok(this.userService.updateAdminPermissions(
                id, updatePermission.getPermissionsToAdd(), updatePermission.getPermissionsToRemove(), authentication.getName()));
    }

    @GetMapping("/moderator-permissions")
    public ResponseEntity<List<ModeratorPermissionsResponse>> getModeratorsWithPermissions() {

        return ResponseEntity.ok(this.userService.getAllModeratorsWithPermissions());
    }

    @PutMapping("/moderator-permissions/{id}")
    public ResponseEntity<ModeratorPermissionsResponse> updateModeratorPermissions(
            @PathVariable UUID id,
            @RequestBody ModeratorPermissionsUpdateRequest updatePermission,
            Authentication authentication) {

        return ResponseEntity.ok(this.userService.updateModerationPermissions(
                id, updatePermission.getPermissionsToAdd(), updatePermission.getPermissionsToRemove(), authentication.getName()));
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<UserPermission>> getPermissions(Authentication authentication) {

        return ResponseEntity.ok(this.userService.getCurrentAdminPermissions(authentication.getName()));
    }
}
