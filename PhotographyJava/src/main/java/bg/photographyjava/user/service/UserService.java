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

    ChangeRoleUserResponse updateUserRole(UUID userId, RoleRequest roleRequest);

    List<BanUserResponse> getAllUsersForBan();

    BanUserResponse banUserAction(UUID id, BanUserReasonRequest banUserReasonRequest);

    BanUserResponse getUserForBan(UUID id);

    List<ApproveUsersResponse> getAllUsersForApprove();

    ApproveUsersResponse approveUserAction(UUID id, ApproveUserReasonRequest approveUserReasonRequest);

    ApproveUsersResponse getUserForApprove(UUID id);

    AdminPermissionsResponse updateAdminPermissions(UUID id, AdminPermissionsUpdateRequest updatePermission);

    List<AdminPermissionsResponse> getAllAdminsWithPermissions();

    List<ModeratorPermissionsResponse> getAllModeratorsWithPermissions();

    ModeratorPermissionsResponse updateModerationPermissions(UUID id, ModeratorPermissionsUpdateRequest updatePermission);

    List<UserEntity> getAllUsersForCountries();

    List<UserPermission> getCurrentAdminPermissions(String username);

    boolean addFriendByUsername(FriendRequest friendRequest, String username);

    boolean followUserByUsername(FollowerUserRequest followerUserRequest, String username);

    boolean acceptFriendRequest(FriendRequest friendRequest, String username);

    boolean rejectFriendRequest(FriendRequest friendRequest, String username);

    boolean isValidUser(String username, String password);

    boolean removeFriendByUsername(FriendRequest friendRequest, String username);

    boolean cancelFriendRequestByUsername(FriendRequest friendRequest, String username);

    boolean unfollowUserByUsername(FollowerUserRequest followerUserRequest, String username);

    boolean areFriends(String username, String targetUsername);

    boolean hasSentFriendRequest(String username, String targetUsername);

    boolean isFollowing(String username, String targetUsername);

    Set<FriendsResponse> getSentFriendRequests(String username);

    Set<FriendsResponse> getReceiveFriendRequests(String username);

    Set<FollowersResponse> getAllFollowers(String username);

    Set<FollowersResponse> getAllFollowings(String username);

    Set<FriendsResponse> getAllFriends(String username);

    boolean removeFollowerByUsername(FollowerUserRequest followerUserRequest, String username);

    boolean blockUser(String username, String blockedUsername);

    boolean unblockUser(String username, String blockedUsername);

    Set<BlockedUserResponse> getBlockedUsers(String username);

    boolean isUserBlocked(String username, String blockedUsername);

    boolean isUserBlockedByMe(String username, String blockerUsername);

    ContactUserResponse getUserDetails(String username);

    UserInformationForPictureResponse getUserById(UUID userId);

    Map<String, String> handleValidationErrors(BindingResult bindingResult);

    boolean editUserProfilePicture(MultipartFile file, String username) throws IOException;
}
