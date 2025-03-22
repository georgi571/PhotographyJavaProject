package bg.photographyjava.user.service;

import bg.photographyjava.user.property.enums.UserPermission;
import bg.photographyjava.web.dto.*;
import bg.photographyjava.user.model.UserEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

public interface UserService {
    void seedUsers();

    Optional<UserEntity> getUserByEmail(String email);

    Optional<UserEntity> getUserByUsername(String username);

    void registerUser(UserRegisterRequest userRegisterRequest);

    String verify(UserLoginRequest userLoginRequest);

    UserProfileResponse getProfileDetails(String username);

    UserEditProfileResponse getProfileEditDetails(String username);

    void editUserDetails(String username, UserEditProfileRequest userEditProfileRequest);

    UserChangeUsernameResponse getUserUsernameDetails(String username);

    UserChangeEmailResponse getUserEmailDetails(String username);

    void editUserUsernameDetails(String username, UserChangeUsernameRequest userChangeUsernameRequest);

    void editUserEmailDetails(String username, UserChangeEmailRequest userChangeEmailRequest);

    void updatePassword(String username, UserChangePasswordRequest userChangePasswordRequest);

    List<ChangeRoleUserResponse> getAllUsers();

    void updateUserRole(UUID userId, String role, String username);

    List<BanUserResponse> getAllUsersForBan();

    void banUserAction(UUID id, BanUserReasonRequest reasonDTO, String username);

    BanUserResponse getUserForBan(UUID id);

    List<ApproveUsersResponse> getAllUsersForApprove();

    void approveUserAction(UUID id, ApproveUserReasonRequest reasonDTO, String username);

    ApproveUsersResponse getUserForApprove(UUID id);

    AdminPermissionsResponse updateAdminPermissions(UUID id, Set<UserPermission> permissionsToAdd, Set<UserPermission> permissionsToRemove, String username);

    List<AdminPermissionsResponse> getAllAdminsWithPermissions();

    List<ModeratorPermissionsResponse> getAllModeratorsWithPermissions();

    ModeratorPermissionsResponse updateModerationPermissions(UUID id, Set<UserPermission> permissionsToAdd, Set<UserPermission> permissionsToRemove, String username);

    List<UserEntity> getAllUsersForCountries();

    List<UserPermission> getCurrentAdminPermissions(String username);

    void addFriendByUsername(FriendRequest friendRequest, String username);

    void followUserByUsername(FollowerUserRequest followerUserRequest, String username);

    void acceptFriendRequest(FriendRequest friendRequest, String username);

    void rejectFriendRequest(FriendRequest friendRequest, String username);

    boolean isValidUser(String username, String password);

    void removeFriendByUsername(FriendRequest friendRequest, String username);

    void cancelFriendRequestByUsername(FriendRequest friendRequest, String username);

    void unfollowUserByUsername(FollowerUserRequest followerUserRequest, String username);

    boolean areFriends(String username, String targetUsername);

    boolean hasSentFriendRequest(String username, String targetUsername);

    boolean isFollowing(String username, String targetUsername);

    Set<FriendsResponse> getSentFriendRequests(String username);

    Set<FriendsResponse> getReceiveFriendRequests(String username);

    Set<FollowersResponse> getAllFollowers(String username);

    Set<FollowersResponse> getAllFollowings(String username);

    Set<FriendsResponse> getAllFriends(String username);

    void removeFollowerByUsername(FollowerUserRequest followerUserRequest, String username);

    void blockUser(String username, String blockedUsername);

    void unblockUser(String username, String blockedUsername);

    Set<BlockedUserResponse> getBlockedUsers(String username);

    boolean isUserBlocked(String username, String blockedUsername);

    ContactUserResponse getUserDetails(String username);

    UserInformationForPictureResponse getUserById(UUID userId);

    Map<String, String> handleValidationErrors(BindingResult bindingResult);

    void editUserProfilePicture(MultipartFile file, String username) throws IOException;
}
