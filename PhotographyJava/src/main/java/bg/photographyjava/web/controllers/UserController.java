package bg.photographyjava.web.controllers;

import bg.photographyjava.web.dto.*;
import bg.photographyjava.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInformationForPictureResponse> getUserById(@PathVariable UUID userId) {

        return ResponseEntity.ok(this.userService.getUserById(userId));
    }

    @GetMapping("/profile/username/{username}")
    public ResponseEntity<UserProfileResponse> getUserInfo(@PathVariable String username) {

        return ResponseEntity.ok(this.userService.getProfileDetails(username));
    }

    @GetMapping("/profile/edit")
    public ResponseEntity<UserEditProfileResponse> getUserDetails(Authentication authentication) {

        return ResponseEntity.ok(this.userService.getProfileEditDetails(authentication.getName()));
    }

    @PutMapping("/profile/edit")
    public ResponseEntity<?> updateUserDetails(Authentication authentication,
                                               @RequestBody @Valid UserEditProfileRequest userEditProfileRequest,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(this.userService.handleValidationErrors(bindingResult));
        }

        this.userService.editUserDetails(authentication.getName(), userEditProfileRequest);

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getProfileEditDetails(authentication.getName()));
    }

    @GetMapping("/profile/edit/username")
    public ResponseEntity<UserChangeUsernameResponse> getUserUsername(Authentication authentication) {

        return ResponseEntity.ok(this.userService.getUserUsernameDetails(authentication.getName()));
    }

    @PutMapping("/profile/edit/username")
    public ResponseEntity<Map<String, String>> updateUserUsername(Authentication authentication,
                                                                  @RequestBody @Valid UserChangeUsernameRequest userChangeUsernameRequest,
                                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(this.userService.handleValidationErrors(bindingResult));
        }

        this.userService.editUserUsernameDetails(authentication.getName(), userChangeUsernameRequest);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Username updated successfully"));
    }

    @GetMapping("/profile/edit/email")
    public ResponseEntity<UserChangeEmailResponse> getUserEmail(Authentication authentication) {

        return ResponseEntity.ok(this.userService.getUserEmailDetails(authentication.getName()));
    }

    @PutMapping("/profile/edit/email")
    public ResponseEntity<Map<String, String>> updateUserEmail(Authentication authentication,
                                                               @RequestBody @Valid UserChangeEmailRequest userChangeEmailRequest,
                                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(this.userService.handleValidationErrors(bindingResult));
        }

        this.userService.editUserEmailDetails(authentication.getName(), userChangeEmailRequest);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Email updated successfully"));
    }


    @PutMapping("/profile/edit/password")
    public ResponseEntity<Map<String, String>> updatePassword(Authentication authentication,
                                                              @RequestBody @Valid UserChangePasswordRequest userChangePasswordRequest,
                                                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(this.userService.handleValidationErrors(bindingResult));
        }

        this.userService.updatePassword(authentication.getName(), userChangePasswordRequest);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Password updated successfully"));
    }

    @PostMapping("/profile/edit/picture")
    public ResponseEntity<Map<String, String>> updatePicture(@RequestParam("file") MultipartFile file,
                                                             Authentication authentication) throws IOException {

        this.userService.editUserProfilePicture(file, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Profile picture successfully updated."));
    }

    @PostMapping("/add-friend")
    public ResponseEntity<Map<String, String>> addFriend(@RequestBody @Valid FriendRequest friendRequest,
                                                         Authentication authentication) {

        this.userService.addFriendByUsername(friendRequest, authentication.getName());

        return ResponseEntity.ok(Map.of("message", "Friend added successfully"));
    }

    @PostMapping("/follow-user")
    public ResponseEntity<Map<String, String>> followUser(@RequestBody @Valid FollowerUserRequest followerUserRequest,
                                                          Authentication authentication) {

        this.userService.followUserByUsername(followerUserRequest, authentication.getName());

        return ResponseEntity.ok(Map.of("message", "User followed successfully"));
    }

    @PostMapping("/accept-friend-request")
    public ResponseEntity<Map<String, String>> acceptFriendRequest(@RequestBody @Valid FriendRequest friendRequest,
                                                                   Authentication authentication) {

        this.userService.acceptFriendRequest(friendRequest, authentication.getName());

        return ResponseEntity.ok(Map.of("message", "Friend request accepted."));
    }

    @PostMapping("/reject-friend-request")
    public ResponseEntity<Map<String, String>> rejectFriendRequest(@RequestBody @Valid FriendRequest friendRequest,
                                                                   Authentication authentication) {

        this.userService.rejectFriendRequest(friendRequest, authentication.getName());

        return ResponseEntity.ok(Map.of("message", "Friend request rejected."));
    }

    @PostMapping("/remove-friend")
    public ResponseEntity<Map<String, String>> removeFriend(@RequestBody FriendRequest friendRequest,
                                                            Authentication authentication) {

        this.userService.removeFriendByUsername(friendRequest, authentication.getName());

        return ResponseEntity.ok(Map.of("message", "Friend removed successfully"));
    }

    @PostMapping("/cancel-friend-request")
    public ResponseEntity<Map<String, String>> cancelFriendRequest(@RequestBody FriendRequest friendRequest,
                                                                   Authentication authentication) {

        this.userService.cancelFriendRequestByUsername(friendRequest, authentication.getName());

        return ResponseEntity.ok(Map.of("message", "Friend request cancelled successfully"));
    }

    @PostMapping("/unfollow-user")
    public ResponseEntity<Map<String, String>> unfollowUser(@RequestBody FollowerUserRequest followerUserRequest,
                                                            Authentication authentication) {

        this.userService.unfollowUserByUsername(followerUserRequest, authentication.getName());

        return ResponseEntity.ok(Map.of("message", "User unfollowed successfully"));
    }

    @PostMapping("/remove-follower")
    public ResponseEntity<Map<String, String>> removeFollower(@RequestBody FollowerUserRequest followerUserRequest,
                                                              Authentication authentication) {

        this.userService.removeFollowerByUsername(followerUserRequest, authentication.getName());

        return ResponseEntity.ok(Map.of("message", "Follower removed successfully"));
    }

    @GetMapping("/are-friends")
    public ResponseEntity<Boolean> checkIfFriends(
            @RequestParam String targetUsername,
            Authentication authentication) {

        return ResponseEntity.ok(this.userService.areFriends(authentication.getName(), targetUsername));
    }

    @GetMapping("/has-sent-friend-request")
    public ResponseEntity<Boolean> checkIfFriendRequestSent(
            @RequestParam String targetUsername,
            Authentication authentication) {

        return ResponseEntity.ok(this.userService.hasSentFriendRequest(authentication.getName(), targetUsername));
    }

    @GetMapping("/is-following")
    public ResponseEntity<Boolean> checkIfFollowing(
            @RequestParam String targetUsername,
            Authentication authentication) {

        return ResponseEntity.ok(this.userService.isFollowing(authentication.getName(), targetUsername));
    }

    @GetMapping("/sent-requests")
    public ResponseEntity<Set<FriendsResponse>> getSentFriendRequests(Authentication authentication) {

        return ResponseEntity.ok(this.userService.getSentFriendRequests(authentication.getName()));
    }

    @GetMapping("/received-requests")
    public ResponseEntity<Set<FriendsResponse>> getReceiveFriendRequests(Authentication authentication) {

        return ResponseEntity.ok(this.userService.getReceiveFriendRequests(authentication.getName()));
    }

    @GetMapping("/followers")
    public ResponseEntity<Set<FollowersResponse>> getAllFollowers(Authentication authentication) {

        return ResponseEntity.ok(this.userService.getAllFollowers(authentication.getName()));
    }

    @GetMapping("/following")
    public ResponseEntity<Set<FollowersResponse>> getAllFollowings(Authentication authentication) {

        return ResponseEntity.ok(this.userService.getAllFollowings(authentication.getName()));
    }

    @GetMapping("/friends")
    public ResponseEntity<Set<FriendsResponse>> getAllFriends(Authentication authentication) {

        return ResponseEntity.ok(this.userService.getAllFriends(authentication.getName()));
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

        return ResponseEntity.ok(this.userService.getBlockedUsers(authentication.getName()));
    }

    @GetMapping("/is-blocked/{username}")
    public ResponseEntity<Boolean> isUserBlocked(@PathVariable String username, Authentication authentication) {

        return ResponseEntity.ok(this.userService.isUserBlocked(authentication.getName(), username));
    }

    @GetMapping("/curr/friends")
    public ResponseEntity<Set<FriendsResponse>> getFriends(@RequestParam String username) {

        return ResponseEntity.ok(this.userService.getAllFriends(username));
    }

    @GetMapping("/curr/followers")
    public ResponseEntity<Set<FollowersResponse>> getFollowers(@RequestParam String username) {

        return ResponseEntity.ok(this.userService.getAllFollowers(username));
    }

    @GetMapping("/user-info")
    public ResponseEntity<ContactUserResponse> getUserInfo(Authentication authentication) {

        return ResponseEntity.ok(this.userService.getUserDetails(authentication.getName()));
    }
}
