package bg.photographyjava.web.controllers;

import bg.photographyjava.user.property.enums.UserPermission;
import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<ChangeRoleUserResponse> updateUserRole(@PathVariable UUID id,
                                                                 @RequestBody RoleRequest roleRequest) {

        return ResponseEntity.ok(this.userService.updateUserRole(id, roleRequest));
    }

    @GetMapping("/ban-users")
    public ResponseEntity<List<BanUserResponse>> getUsersForBan() {

        return ResponseEntity.ok(this.userService.getAllUsersForBan());
    }


    @PutMapping("/ban-users/{id}")
    public ResponseEntity<BanUserResponse> banOrUnbanUser(@PathVariable UUID id,
                                                          @RequestBody BanUserReasonRequest request) {

        return ResponseEntity.ok(this.userService.banUserAction(id, request));
    }

    @GetMapping("/approve-users")
    public ResponseEntity<List<ApproveUsersResponse>> getUsersForApprove() {

        return ResponseEntity.ok(this.userService.getAllUsersForApprove());
    }

    @PutMapping("/approve-users/{id}")
    public ResponseEntity<ApproveUsersResponse> approveUser(@PathVariable UUID id,
                                                            @RequestBody ApproveUserReasonRequest request) {

        return ResponseEntity.ok(this.userService.approveUserAction(id, request));
    }

    @GetMapping("/admin-permissions")
    public ResponseEntity<List<AdminPermissionsResponse>> getAdminsWithPermissions() {

        return ResponseEntity.ok(this.userService.getAllAdminsWithPermissions());
    }

    @PutMapping("/admin-permissions/{id}")
    public ResponseEntity<AdminPermissionsResponse> updateAdminPermissions(@PathVariable UUID id,
                                                                           @RequestBody AdminPermissionsUpdateRequest updatePermission) {

        return ResponseEntity.ok(this.userService.updateAdminPermissions(id, updatePermission));
    }

    @GetMapping("/moderator-permissions")
    public ResponseEntity<List<ModeratorPermissionsResponse>> getModeratorsWithPermissions() {

        return ResponseEntity.ok(this.userService.getAllModeratorsWithPermissions());
    }

    @PutMapping("/moderator-permissions/{id}")
    public ResponseEntity<ModeratorPermissionsResponse> updateModeratorPermissions(@PathVariable UUID id,
                                                                                   @RequestBody ModeratorPermissionsUpdateRequest updatePermission) {

        return ResponseEntity.ok(this.userService.updateModerationPermissions(id, updatePermission));
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<UserPermission>> getPermissions(Authentication authentication) {

        return ResponseEntity.ok(this.userService.getCurrentAdminPermissions(authentication.getName()));
    }
}
