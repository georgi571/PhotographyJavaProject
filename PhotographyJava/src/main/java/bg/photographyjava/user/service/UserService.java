package bg.photographyjava.user.service;

import bg.photographyjava.user.property.enums.UserPermission;
import bg.photographyjava.web.dto.*;
import bg.photographyjava.user.model.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    void seedUsers();

    Optional<UserEntity> getUserByEmail(String email);

    Optional<UserEntity> getUserByUsername(String username);

    void registerUser(UserRegisterDTO userRegisterDTO);

    String verify(UserLoginRequest userLoginRequest);

    UserProfileDTO getProfileDetails(String username);

    UserEditProfileDTO getProfileEditDetails(String username);

    void editUserDetails(String username, UserEditProfileDTO userEditProfileDTO);

    UserChangeUsernameDTO getUserUsernameDetails(String username);

    UserChangeEmailDTO getUserEmailDetails(String username);

    void editUserUsernameDetails(String username, UserChangeUsernameDTO userChangeUsernameDTO);

    void editUserEmailDetails(String username, UserChangeEmailDTO userChangeEmailDTO);

    void updatePassword(String username, String encodedNewPassword);

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

    void addFriendByUsername(AddFriendDTO addFriendDTO, String username);

    void followUserByUsername(FollowUserDTO followUserDTO, String username);

    void acceptFriendRequest(AddFriendDTO addFriendDTO, String username);

    void rejectFriendRequest(AddFriendDTO addFriendDTO, String username);

    boolean isValidUser(String username, String password);
}
