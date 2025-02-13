package bg.photographyjava.web.controllers;

import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.web.dto.*;
import bg.photographyjava.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // return full information about the user details

    @GetMapping("profile/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable String username, Authentication authentication) {
        UserProfileDTO userProfileDTO = this.userService.getProfileDetails(username);

        return ResponseEntity.ok(userProfileDTO);
    }

    // get user details information for edit page

    @GetMapping("profile/edit")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        String username = authentication.getName();
        UserEditProfileDTO userEditProfileDTO = this.userService.getProfileEditDetails(username);

        return ResponseEntity.ok(userEditProfileDTO);
    }

    // change user details information

    @PutMapping("profile/edit")
    public ResponseEntity<?> updateUserDetails(Authentication authentication, @RequestBody @Valid UserEditProfileDTO userEditProfileDTO,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorResponse = new HashMap<>();

            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errorResponse.put(fieldName, errorMessage);
            });

            return ResponseEntity.badRequest().body(errorResponse);
        }

        String username = authentication.getName();

        this.userService.editUserDetails(username, userEditProfileDTO);

        UserEditProfileDTO updatedProfileDTO = this.userService.getProfileEditDetails(username);

        return ResponseEntity.status(HttpStatus.OK).body(updatedProfileDTO);
    }

    @GetMapping("profile/edit/username")
    public ResponseEntity<?> getUserUsername(Authentication authentication) {
        String username = authentication.getName();
        UserChangeUsernameDTO userChangeUsernameDTO = this.userService.getUserUsernameDetails(username);

        return ResponseEntity.ok(userChangeUsernameDTO);
    }

    @PutMapping("profile/edit/username")
    public ResponseEntity<?> updateUserUsername(Authentication authentication, @RequestBody @Valid UserChangeUsernameDTO userChangeUsernameDTO,
                                                BindingResult bindingResult) {
        Map<String, String> errorResponse = new HashMap<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errorResponse.put(fieldName, errorMessage);
            });

            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (this.userService.getUserByEmail(userChangeUsernameDTO.getOldUsername()).isEmpty()) {
            String fieldName = "oldUsername";
            String errorMessage = "Invalid old username";
            errorResponse.put(fieldName, errorMessage);

            return ResponseEntity.badRequest().body(errorResponse);
        }

        String username = authentication.getName();

        this.userService.editUserUsernameDetails(username, userChangeUsernameDTO);

        UserChangeUsernameDTO updateUsername = this.userService.getUserUsernameDetails(username);

        return ResponseEntity.status(HttpStatus.OK).body(updateUsername);
    }

    @GetMapping("profile/edit/email")
    public ResponseEntity<?> getUserEmail(Authentication authentication) {
        String username = authentication.getName();
        UserChangeEmailDTO userChangeEmailDTO = this.userService.getUserEmailDetails(username);

        return ResponseEntity.ok(userChangeEmailDTO);
    }

    @PutMapping("profile/edit/email")
    public ResponseEntity<?> updateUserEmail(Authentication authentication, @RequestBody @Valid UserChangeEmailDTO userChangeEmailDTO,
                                             BindingResult bindingResult) {

        Map<String, String> errorResponse = new HashMap<>();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errorResponse.put(fieldName, errorMessage);
            });

            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (this.userService.getUserByEmail(userChangeEmailDTO.getOldEmail()).isEmpty()) {
            String fieldName = "oldEmail";
            String errorMessage = "Invalid old email address";
            errorResponse.put(fieldName, errorMessage);

            return ResponseEntity.badRequest().body(errorResponse);
        }

        String username = authentication.getName();

        this.userService.editUserEmailDetails(username, userChangeEmailDTO);

        UserChangeEmailDTO updateEmail = this.userService.getUserEmailDetails(username);

        return ResponseEntity.status(HttpStatus.OK).body(updateEmail);
    }


    @PutMapping("profile/edit/password")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody @Valid UserChangePasswordDTO userChangePasswordDTO,
                                            BindingResult bindingResult) {

        Map<String, String> errorResponse = new HashMap<>();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errorResponse.put(fieldName, errorMessage);
            });

            return ResponseEntity.badRequest().body(errorResponse);
        }

        String username = authentication.getName();

        UserEntity user = this.userService.getUserByUsername(username).get();

        if (!passwordEncoder.matches(userChangePasswordDTO.getOldPassword(), user.getPassword())) {
            String fieldName = "currentPassword";
            String errorMessage = "Invalid current password";
            return ResponseEntity.badRequest().body(errorResponse);
        }

        String encodedNewPassword = passwordEncoder.encode(userChangePasswordDTO.getNewPassword());
        this.userService.updatePassword(username, encodedNewPassword);

        return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
    }

    @PostMapping("/add-friend")
    public ResponseEntity<String> addFriend(@RequestBody AddFriendDTO addFriendDTO, Authentication authentication) {
        String username = authentication.getName();
        this.userService.addFriendByUsername(addFriendDTO, username);
        return ResponseEntity.ok("Friend added successfully");
    }

    @PostMapping("/follow-user")
    public ResponseEntity<String> followUser(@RequestBody FollowUserDTO followUserDTO, Authentication authentication) {
        String username = authentication.getName();
        this.userService.followUserByUsername(followUserDTO, username);
        return ResponseEntity.ok("User followed successfully");
    }

    @PostMapping("/accept-friend-request")
    public ResponseEntity<String> acceptFriendRequest(@RequestBody AddFriendDTO addFriendDTO, Authentication authentication) {
        String username = authentication.getName();
        userService.acceptFriendRequest(addFriendDTO, username);
        return ResponseEntity.ok("Friend request accepted.");
    }

    // Reject a friend request
    @PostMapping("/reject-friend-request")
    public ResponseEntity<String> rejectFriendRequest(@RequestBody AddFriendDTO addFriendDTO, Authentication authentication) {
        String username = authentication.getName();
        userService.rejectFriendRequest(addFriendDTO, username);
        return ResponseEntity.ok("Friend request rejected.");
    }
}
