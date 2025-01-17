package bg.photographyjava.web.controllers;

import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/change-roles")
    public List<ChangeRoleUserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // Update a user's role
    @PutMapping("/change-roles/{userId}")
    public ResponseEntity<?> updateUserRole(@PathVariable UUID userId, @RequestBody RoleDTO roleDTO, Authentication authentication) {
        String username = authentication.getName();
        userService.updateUserRole(userId, roleDTO.getRole(), username);
        return ResponseEntity.ok(Map.of("message", "Role updated successfully"));
    }

    @GetMapping("/ban-users")
    public ResponseEntity<?> getUsersForBan() {
        return ResponseEntity.ok(this.userService.getAllUsersForBan());
    }


    @PutMapping("/ban-users/{id}")
    public ResponseEntity<?> banOrUnbanUser(@PathVariable UUID id,
                                            @RequestBody BanUserReasonDTO request,
                                            Authentication authentication) {
        String username = authentication.getName();
        this.userService.banUserAction(id, request, username);

        BanUserDTO userForBan = this.userService.getUserForBan(id);

        return ResponseEntity.ok(userForBan);
    }

    @GetMapping("/approve-users")
    public ResponseEntity<?> getUsersForApprove() {
        return ResponseEntity.ok(this.userService.getAllUsersForApprove());
    }


    @PutMapping("/approve-users/{id}")
    public ResponseEntity<?> approvingUser(@PathVariable UUID id,
                                            @RequestBody ApproveUserReasonDTO request,
                                            Authentication authentication) {
        String username = authentication.getName();
        this.userService.approveUserAction(id, request, username);

        ApproveUsersDTO userForApprove = this.userService.getUserForApprove(id);

        return ResponseEntity.ok(userForApprove);
    }
}
