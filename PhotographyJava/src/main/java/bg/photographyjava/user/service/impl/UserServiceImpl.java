package bg.photographyjava.user.service.impl;

import bg.photographyjava.exception.InvalidPasswordException;
import bg.photographyjava.exception.OldEmailMismatchException;
import bg.photographyjava.exception.OldUsernameMismatchException;
import bg.photographyjava.exception.UserNotFoundException;
import bg.photographyjava.shared.service.CloudinaryService;
import bg.photographyjava.user.model.Role;
import bg.photographyjava.user.property.enums.*;
import bg.photographyjava.web.dto.*;
import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.user.repository.CountryRepository;
import bg.photographyjava.user.repository.RankRepository;
import bg.photographyjava.user.repository.RoleRepository;
import bg.photographyjava.user.repository.UserRepository;
import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.filter.JWTService;
import bg.photographyjava.web.mapper.DtoMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RankRepository userRankRepository;
    private final RoleRepository userRoleRepository;
    private final CountryRepository countryRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final CloudinaryService cloudinaryService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RankRepository userRankRepository, RoleRepository userRoleRepository, CountryRepository countryRepository, AuthenticationManager authenticationManager, JWTService jwtService, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRankRepository = userRankRepository;
        this.userRoleRepository = userRoleRepository;
        this.countryRepository = countryRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public void seedUsers() {
        if (this.userRepository.count() == 0) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setBirthDate(LocalDate.of(2025, 1, 1));
            admin.setEmail("admin@gmail.com");
            admin.setCountry(this.countryRepository.findByName(CountryEnum.BULGARIA));
            admin.setCity("Blagoevgrad");
            admin.setGender(GenderEnum.MALE);
            admin.setRank(this.userRankRepository.findByRank(UserRank.MASTER));
            admin.setRole(this.userRoleRepository.findByRole(UserRole.ADMIN));
            admin.setApproved(true);
            admin.setRealName("Admin Admin");
            admin.setPoints(2500);
            admin.setApproved(true);
            admin.setBanned(false);
            admin.setProfilePicturePath("https://res.cloudinary.com/dkyp0c0lz/image/upload/v1737304170/male-profile-picture_rltohq.avif");
            admin.setPermissions(Set.of(UserPermission.APPROVE_USERS, UserPermission.CHANGE_USER_ROLES, UserPermission.BAN_USERS, UserPermission.ANSWER_FEEDBACK, UserPermission.DELETE_MESSAGE, UserPermission.DELETE_PICTURE));
            this.userRepository.saveAndFlush(admin);
        }
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> getUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public void registerUser(UserRegisterRequest userRegisterRequest) {
        UserEntity user = DtoMapper.mapUserRegisterRequestToUserEntity(userRegisterRequest);
        user.setPassword(this.passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setCountry(this.countryRepository.findByName(CountryEnum.fromString(userRegisterRequest.getCountry())));
        user.setRank(this.userRankRepository.findByRank(UserRank.BEGINNER));
        user.setRole(this.userRoleRepository.findByRole(UserRole.USER));
        this.userRepository.saveAndFlush(user);
    }

    @Override
    public String verify(UserLoginRequest userLoginRequest) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            String role = authentication.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElseThrow(() -> new IllegalStateException("User has no roles assigned"));

            UserEntity user = this.getUserByUsername(userLoginRequest.getUsername()).orElseThrow(() ->
                    new UserNotFoundException("User with username " + userLoginRequest.getUsername() + " not found"));
//            UserEntity user = this.userRepository.findByUsername(userLoginRequest.getUsername()).get();
            List<UserPermission> userPermissions = user.getPermissions().stream().toList();

            return jwtService.generateToken(userLoginRequest.getUsername(), role, userPermissions, user.getId());
        }
        return "Failed";
    }

    @Override
    public UserProfileResponse getProfileDetails(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        return DtoMapper.mapUserEntityToUserProfileResponse(user);
    }

    @Override
    public UserEditProfileResponse getProfileEditDetails(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        return DtoMapper.mapUserEntityToUserEditProfileResponse(user);
    }

    @Override
    public void editUserDetails(String username, UserEditProfileRequest userEditProfileRequest) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        DtoMapper.mapUserEditProfileRequestToUserEntity(user, userEditProfileRequest);

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public UserChangeUsernameResponse getUserUsernameDetails(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        return DtoMapper.mapUserEntityToUserChangeUsernameResponse(user);
    }

    @Override
    public UserChangeEmailResponse getUserEmailDetails(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        return DtoMapper.mapUserEntityToUserChangeEmailResponse(user);
    }

    @Override
    public void editUserUsernameDetails(String username, UserChangeUsernameRequest userChangeUsernameRequest) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        if (!passwordEncoder.matches(userChangeUsernameRequest.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid current password");
        }

        if (!username.equals(userChangeUsernameRequest.getOldUsername())) {
            throw new OldUsernameMismatchException("Old username does not match the authenticated username.");
        }

        DtoMapper.mapUserChangeUsernameRequestToUserEntity(user, userChangeUsernameRequest);

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public void editUserEmailDetails(String username, UserChangeEmailRequest userChangeEmailRequest) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        if (!passwordEncoder.matches(userChangeEmailRequest.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid current password");
        }

        if (!user.getEmail().equals(userChangeEmailRequest.getOldEmail())) {
            throw new OldEmailMismatchException("Old email does not match the authenticated user email.");
        }

        DtoMapper.mapUserChangeEmailRequestToUserEntity(user, userChangeEmailRequest);

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public void updatePassword(String username, UserChangePasswordRequest userChangePasswordRequest) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid current password");
        }

        String encodedNewPassword = passwordEncoder.encode(userChangePasswordRequest.getNewPassword());

        user.setPassword(encodedNewPassword);

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public List<ChangeRoleUserResponse> getAllUsers() {
        return this.userRepository.findAll()
                .stream()
                .map(DtoMapper::mapUserEntityToChangeRoleUserResponse)
                .toList();
    }

    @Override
    public void updateUserRole(UUID userId, String roleToChange, String username) {
        UserEntity admin = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserRole userRole = Enum.valueOf(UserRole.class, roleToChange);
        Role role = this.userRoleRepository.findByRole(userRole);

        user.getPermissions().clear();

        user.setRole(role);
        userRepository.saveAndFlush(user);
    }

    @Override
    public List<BanUserResponse> getAllUsersForBan() {
        return userRepository.findAll()
                .stream()
                .map(DtoMapper::mapUserEntityToBanUserResponse)
                .toList();
    }

    @Override
    public void banUserAction(UUID id, BanUserReasonRequest reasonDTO, String username) {
        UserEntity admin = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity user = this.userRepository.findById(id).get();
        if (reasonDTO.getAction().equals("ban")) {
            user.setBanned(true);
            user.setReasonForBan(reasonDTO.getReason());
        } else {
            user.setBanned(false);
            user.setReasonForBan(null);
        }

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public BanUserResponse getUserForBan(UUID id) {
        UserEntity user = this.userRepository.findById(id).get();

        return DtoMapper.mapUserEntityToBanUserResponse(user);
    }

    @Override
    public List<ApproveUsersResponse> getAllUsersForApprove() {
        return userRepository.findAll()
                .stream()
                .filter(userEntity -> !userEntity.isApproved() && !userEntity.isBanned())
                .map(DtoMapper::mapUserEntityToApproveUsersResponse).toList();
    }

    @Override
    public void approveUserAction(UUID id, ApproveUserReasonRequest reasonRequest, String username) {
        UserEntity admin = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity user = this.userRepository.findById(id).get();
        if (reasonRequest.getAction().equals("approve")) {
            user.setApproved(true);
        } else {
            user.setApproved(false);
            user.setBanned(true);
            user.setReasonForBan(reasonRequest.getReason());
        }

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public ApproveUsersResponse getUserForApprove(UUID id) {
        UserEntity user = this.userRepository.findById(id).get();

        return DtoMapper.mapUserEntityToApproveUsersResponse(user);
    }

    @Override
    public List<AdminPermissionsResponse> getAllAdminsWithPermissions() {
        return this.userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && user.getRole().getRole() == UserRole.ADMIN)
                .map(DtoMapper::mapUserEntityToAdminPermissionsResponse)
                .toList();
    }

    @Override
    public AdminPermissionsResponse updateAdminPermissions(UUID id, Set<UserPermission> permissionsToAdd, Set<UserPermission> permissionsToRemove, String username) {
        UserEntity superAdmin = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity admin = this.userRepository.findById(id).get();

        admin.getPermissions().addAll(permissionsToAdd);
        admin.getPermissions().removeAll(permissionsToRemove);

        this.userRepository.saveAndFlush(admin);

        return DtoMapper.mapUserEntityToAdminPermissionsResponse(admin);
    }

    @Override
    public List<ModeratorPermissionsResponse> getAllModeratorsWithPermissions() {
        return this.userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && user.getRole().getRole() == UserRole.MODERATOR)
                .map(DtoMapper::mapUserEntityToModeratorPermissionsResponse)
                .toList();
    }

    @Override
    public ModeratorPermissionsResponse updateModerationPermissions(UUID id, Set<UserPermission> permissionsToAdd, Set<UserPermission> permissionsToRemove, String username) {
        UserEntity superAdmin = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity moderator = this.userRepository.findById(id).get();

        moderator.getPermissions().addAll(permissionsToAdd);
        moderator.getPermissions().removeAll(permissionsToRemove);

        this.userRepository.saveAndFlush(moderator);

        return DtoMapper.mapUserEntityToModeratorPermissionsResponse(moderator);
    }

    @Override
    public List<UserEntity> getAllUsersForCountries() {
        return this.userRepository.findAll();
    }

    @Override
    public List<UserPermission> getCurrentAdminPermissions(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        return user.getPermissions().stream().toList();
    }

    @Override
    public void addFriendByUsername(FriendRequest friendRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity friend = this.getUserByUsername(friendRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + friendRequest.getUsername() + " not found"));

        user.getSendFriendRequest().add(friend);
        friend.getReceiveFriendRequest().add(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);
    }

    @Override
    public void followUserByUsername(FollowerUserRequest followerUserRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity follower = this.getUserByUsername(followerUserRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + followerUserRequest.getUsername() + " not found"));

        user.getFollowing().add(follower);
        follower.getFollowers().add(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(follower);
    }

    @Override
    public void acceptFriendRequest(FriendRequest friendRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity friend = this.getUserByUsername(friendRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + friendRequest.getUsername() + " not found"));

        user.getFriends().add(friend);
        friend.getFriends().add(user);

        user.getReceiveFriendRequest().remove(friend);
        friend.getSendFriendRequest().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);
    }

    @Override
    public void rejectFriendRequest(FriendRequest friendRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity friend = this.getUserByUsername(friendRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + friendRequest.getUsername() + " not found"));

        user.getReceiveFriendRequest().remove(friend);
        friend.getSendFriendRequest().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);
    }

    @Override
    public boolean isValidUser(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    @Override
    public void removeFriendByUsername(FriendRequest friendRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity friend = this.getUserByUsername(friendRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + friendRequest.getUsername() + " not found"));

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);
    }

    @Override
    public void cancelFriendRequestByUsername(FriendRequest friendRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity friend = this.getUserByUsername(friendRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + friendRequest.getUsername() + " not found"));

        user.getSendFriendRequest().remove(friend);
        friend.getReceiveFriendRequest().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);
    }

    @Override
    public void unfollowUserByUsername(FollowerUserRequest followerUserRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity follower = this.getUserByUsername(followerUserRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + followerUserRequest.getUsername() + " not found"));

        user.getFollowing().remove(follower);
        follower.getFollowers().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(follower);
    }

    @Override
    public boolean areFriends(String username, String targetUsername) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity targetUser = this.getUserByUsername(targetUsername).orElseThrow(() ->
                new UserNotFoundException("User with username " + targetUsername + " not found"));

        return user.getFriends().contains(targetUser);
    }

    @Override
    public boolean hasSentFriendRequest(String username, String targetUsername) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity targetUser = this.getUserByUsername(targetUsername).orElseThrow(() ->
                new UserNotFoundException("User with username " + targetUsername + " not found"));

        return user.getSendFriendRequest().contains(targetUser);
    }

    @Override
    public boolean isFollowing(String username, String targetUsername) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity targetUser = this.getUserByUsername(targetUsername).orElseThrow(() ->
                new UserNotFoundException("User with username " + targetUsername + " not found"));

        return targetUser.getFollowers().contains(user);
    }

    @Override
    public Set<FriendsResponse> getSentFriendRequests(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        Set<UserEntity> friendRequest = user.getSendFriendRequest();
        return friendRequest.stream()
                .map(DtoMapper::mapUserEntityToFriendsResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FriendsResponse> getReceiveFriendRequests(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        Set<UserEntity> receiveRequest = user.getReceiveFriendRequest();
        return receiveRequest.stream()
                .map(DtoMapper::mapUserEntityToFriendsResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FollowersResponse> getAllFollowers(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        Set<UserEntity> followers = user.getFollowers();
        return followers.stream()
                .map(DtoMapper::mapUserEntityToFollowersResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FollowersResponse> getAllFollowings(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        Set<UserEntity> followings = user.getFollowing();
        return followings.stream()
                .map(DtoMapper::mapUserEntityToFollowersResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FriendsResponse> getAllFriends(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        Set<UserEntity> friends = user.getFriends();
        return friends.stream()
                .map(DtoMapper::mapUserEntityToFriendsResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public void removeFollowerByUsername(FollowerUserRequest followerUserRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity follower = this.getUserByUsername(followerUserRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + followerUserRequest.getUsername() + " not found"));

        user.getFollowers().remove(follower);
        follower.getFollowing().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(follower);
    }

    @Override
    public void blockUser(String username, String blockedUsername) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity blockedUser = this.getUserByUsername(blockedUsername).orElseThrow(() ->
                new UserNotFoundException("User with username " + blockedUsername + " not found"));

        user.getFriends().remove(blockedUser);
        user.getReceiveFriendRequest().remove(blockedUser);
        user.getSendFriendRequest().remove(blockedUser);
        user.getFollowing().remove(blockedUser);
        user.getFollowers().remove(blockedUser);

        blockedUser.getFriends().remove(user);
        blockedUser.getReceiveFriendRequest().remove(user);
        blockedUser.getSendFriendRequest().remove(user);
        blockedUser.getFollowing().remove(user);
        blockedUser.getFollowers().remove(user);

        user.getBlockedUsers().add(blockedUser);
        userRepository.saveAndFlush(user);
        userRepository.saveAndFlush(blockedUser);
    }

    @Override
    public void unblockUser(String username, String blockedUsername) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity blockedUser = this.getUserByUsername(blockedUsername).orElseThrow(() ->
                new UserNotFoundException("User with username " + blockedUsername + " not found"));

        user.getBlockedUsers().remove(blockedUser);
        this.userRepository.saveAndFlush(user);
    }

    @Override
    public Set<BlockedUserResponse> getBlockedUsers(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        Set<UserEntity> blockedUsers = user.getBlockedUsers();

        return blockedUsers.stream()
                .map(DtoMapper::mapUserEntityToBlockedUserResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isUserBlocked(String username, String blockedUsername) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity blockedUser = this.getUserByUsername(blockedUsername).orElseThrow(() ->
                new UserNotFoundException("User with username " + blockedUsername + " not found"));

        return blockedUser.getBlockedUsers().contains(user);
    }

    @Override
    public ContactUserResponse getUserDetails(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        return DtoMapper.mapUserEntityToContactUserResponse(user);
    }

    @Override
    public UserInformationForPictureResponse getUserById(UUID userId) {
        UserEntity user = this.userRepository.findById(userId).get();

        return DtoMapper.mapUserEntityToUserInformationForPictureResponse(user);
    }

    @Override
    public Map<String, String> handleValidationErrors(BindingResult bindingResult) {
        Map<String, String> errorResponse = new HashMap<>();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errorResponse.put(fieldName, errorMessage);
            });
        }
        return errorResponse;
    }

    @Override
    public void editUserProfilePicture(MultipartFile file, String username) throws IOException {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        Map<String, Object> uploadResult = this.cloudinaryService.uploadImage(file);
        String pictureFilePath = (String) uploadResult.get("secure_url");

        user.setProfilePicturePath(pictureFilePath);

        this.userRepository.saveAndFlush(user);
    }
}
