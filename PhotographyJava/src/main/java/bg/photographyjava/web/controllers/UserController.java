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
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // return full information about the user details

    @GetMapping("/profile/username/{username}")
    public ResponseEntity<UserProfileDTO> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(this.userService.getProfileDetails(username));
    }

    // get user details information for edit page

    @GetMapping("/profile/edit")
    public ResponseEntity<UserEditProfileDTO> getUserDetails(Authentication authentication) {
        return ResponseEntity.ok(this.userService.getProfileEditDetails(authentication.getName()));
    }

    // change user details information

    @PutMapping("/profile/edit")
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

    @GetMapping("/profile/edit/username")
    public ResponseEntity<UserChangeUsernameDTO> getUserUsername(Authentication authentication) {
        return ResponseEntity.ok(this.userService.getUserUsernameDetails(authentication.getName()));
    }

    @PutMapping("/profile/edit/username")
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

    @GetMapping("/profile/edit/email")
    public ResponseEntity<UserChangeEmailDTO> getUserEmail(Authentication authentication) {
        return ResponseEntity.ok(this.userService.getUserEmailDetails(authentication.getName()));
    }

    @PutMapping("/profile/edit/email")
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


    @PutMapping("/profile/edit/password")
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
    public ResponseEntity<Map<String, String>> addFriend(@RequestBody AddFriendDTO addFriendDTO, Authentication authentication) {
        this.userService.addFriendByUsername(addFriendDTO, authentication.getName());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Friend added successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/follow-user")
    public ResponseEntity<Map<String, String>> followUser(@RequestBody FollowerUserRequest followerUserRequest, Authentication authentication) {
        this.userService.followUserByUsername(followerUserRequest, authentication.getName());
        Map<String, String> response = new HashMap<>();
        response.put("message", "User followed successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accept-friend-request")
    public ResponseEntity<Map<String, String>> acceptFriendRequest(@RequestBody AddFriendDTO addFriendDTO, Authentication authentication) {
        this.userService.acceptFriendRequest(addFriendDTO, authentication.getName());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Friend request accepted.");
        return ResponseEntity.ok(response);
    }

    // Reject a friend request
    @PostMapping("/reject-friend-request")
    public ResponseEntity<Map<String, String>> rejectFriendRequest(@RequestBody AddFriendDTO addFriendDTO, Authentication authentication) {
        this.userService.rejectFriendRequest(addFriendDTO, authentication.getName());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Friend request rejected.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove-friend")
    public ResponseEntity<Map<String, String>> removeFriend(@RequestBody AddFriendDTO addFriendDTO, Authentication authentication) {
        this.userService.removeFriendByUsername(addFriendDTO, authentication.getName());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Friend removed successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel-friend-request")
    public ResponseEntity<Map<String, String>> cancelFriendRequest(@RequestBody AddFriendDTO addFriendDTO, Authentication authentication) {
        this.userService.cancelFriendRequestByUsername(addFriendDTO, authentication.getName());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Friend request cancelled successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unfollow-user")
    public ResponseEntity<Map<String, String>> unfollowUser(@RequestBody FollowerUserRequest followerUserRequest, Authentication authentication) {
        this.userService.unfollowUserByUsername(followerUserRequest, authentication.getName());
        Map<String, String> response = new HashMap<>();
        response.put("message", "User unfollowed successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove-follower")
    public ResponseEntity<Map<String, String>> removeFollower(@RequestBody FollowerUserRequest followerUserRequest, Authentication authentication) {
        this.userService.removeFollowerByUsername(followerUserRequest, authentication.getName());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Follower removed successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/are-friends")
    public ResponseEntity<Boolean> checkIfFriends(
            @RequestParam String targetUsername,
            Authentication authentication) {
        String username = authentication.getName();
        boolean areFriends = this.userService.areFriends(username, targetUsername);
        return ResponseEntity.ok(areFriends);
    }

    @GetMapping("/has-sent-friend-request")
    public ResponseEntity<Boolean> checkIfFriendRequestSent(
            @RequestParam String targetUsername,
            Authentication authentication) {
        String username = authentication.getName();
        boolean hasSentRequest = this.userService.hasSentFriendRequest(username, targetUsername);
        return ResponseEntity.ok(hasSentRequest);
    }

    @GetMapping("/is-following")
    public ResponseEntity<Boolean> checkIfFollowing(
            @RequestParam String targetUsername,
            Authentication authentication) {
        String username = authentication.getName();
        boolean isFollowing = this.userService.isFollowing(username, targetUsername);
        return ResponseEntity.ok(isFollowing);
    }

    @GetMapping("/sent-requests")
    public ResponseEntity<Set<FriendsResponse>> getSentFriendRequests(Authentication authentication) {
        String username = authentication.getName();
        Set<FriendsResponse> sentRequests = this.userService.getSentFriendRequests(username);
        return ResponseEntity.ok(sentRequests);
    }

    @GetMapping("/received-requests")
    public ResponseEntity<Set<FriendsResponse>> getReceiveFriendRequests(Authentication authentication) {
        String username = authentication.getName();
        Set<FriendsResponse> receiveRequests = this.userService.getReceiveFriendRequests(username);
        return ResponseEntity.ok(receiveRequests);
    }

    @GetMapping("/followers")
    public ResponseEntity<Set<FollowersResponse>> getAllFollowers(Authentication authentication) {
        String username = authentication.getName();
        Set<FollowersResponse> followersResponse = this.userService.getAllFollowers(username);
        return ResponseEntity.ok(followersResponse);
    }

    @GetMapping("/following")
    public ResponseEntity<Set<FollowersResponse>> getAllFollowings(Authentication authentication) {
        String username = authentication.getName();
        Set<FollowersResponse> followersResponse = this.userService.getAllFollowings(username);
        return ResponseEntity.ok(followersResponse);
    }

    @GetMapping("/friends")
    public ResponseEntity<Set<FriendsResponse>> getAllFriends(Authentication authentication) {
        String username = authentication.getName();
        Set<FriendsResponse> friendsResponses = this.userService.getAllFriends(username);
        return ResponseEntity.ok(friendsResponses);
    }

    @PostMapping("/block")
    public ResponseEntity<String> blockUser(@RequestBody String blockedUsername, Authentication authentication) {
        this.userService.blockUser(authentication.getName(), blockedUsername);
        return ResponseEntity.ok("User blocked successfully.");
    }

    @PostMapping("/unblock")
    public ResponseEntity<String> unblockUser(@RequestBody String blockedUsername, Authentication authentication) {
        this.userService.unblockUser(authentication.getName(), blockedUsername);
        return ResponseEntity.ok("User unblocked successfully.");
    }

    @GetMapping("/blocked-users")
    public ResponseEntity<Set<BlockedUserResponse>> getBlockedUsers(Authentication authentication) {
        Set<BlockedUserResponse> blockedUsers = userService.getBlockedUsers(authentication.getName());
        return ResponseEntity.ok(blockedUsers);
    }

    @GetMapping("/is-blocked/{username}")
    public ResponseEntity<Boolean> isUserBlocked(@PathVariable String username, Authentication authentication) {
        boolean isBlocked = userService.isUserBlocked(authentication.getName(), username);
        return ResponseEntity.ok(isBlocked);
    }

    @GetMapping("/curr/friends")
    public ResponseEntity<Set<FriendsResponse>> getFriends(@RequestParam String username) {
        Set<FriendsResponse> friends = this.userService.getAllFriends(username);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/curr/followers")
    public ResponseEntity<Set<FollowersResponse>> getFollowers(@RequestParam String username) {
        Set<FollowersResponse> followers = this.userService.getAllFollowers(username);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/user-info")
    public ResponseEntity<ContactUserResponse> getUserInfo(Authentication authentication) {

        return ResponseEntity.ok(this.userService.getUserDetails(authentication.getName()));
    }
}
