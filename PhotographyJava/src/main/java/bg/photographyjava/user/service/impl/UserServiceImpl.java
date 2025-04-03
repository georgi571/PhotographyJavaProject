package bg.photographyjava.user.service.impl;

import bg.photographyjava.exception.*;
import bg.photographyjava.shared.service.CloudinaryService;
import bg.photographyjava.shared.service.impl.KafkaProducer;
import bg.photographyjava.user.model.Role;
import bg.photographyjava.user.property.enums.*;
import bg.photographyjava.web.dto.*;
import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.user.repository.CountryRepository;
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
    private final RoleRepository userRoleRepository;
    private final CountryRepository countryRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final CloudinaryService cloudinaryService;
    private final KafkaProducer kafkaProducer;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository userRoleRepository, CountryRepository countryRepository, AuthenticationManager authenticationManager, JWTService jwtService, CloudinaryService cloudinaryService, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleRepository = userRoleRepository;
        this.countryRepository = countryRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.cloudinaryService = cloudinaryService;
        this.kafkaProducer = kafkaProducer;
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
            admin.setRole(this.userRoleRepository.findByRole(UserRole.ADMIN));
            admin.setApproved(true);
            admin.setRealName("Admin Admin");
            admin.setApproved(true);
            admin.setBanned(false);
            admin.setProfilePicturePath("https://res.cloudinary.com/dkyp0c0lz/image/upload/v1737304170/male-profile-picture_rltohq.avif");
            admin.setPermissions(Set.of(UserPermission.APPROVE_USERS, UserPermission.CHANGE_USER_ROLES, UserPermission.BAN_USERS, UserPermission.ANSWER_FEEDBACK, UserPermission.DELETE_MESSAGE, UserPermission.DELETE_PICTURE, UserPermission.MANAGE_CHALLENGE));
            this.userRepository.saveAndFlush(admin);

            kafkaProducer.sendMessage(DtoMapper.mapUserEntityToUserRegisterV1(admin));
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
        user.setRole(this.userRoleRepository.findByRole(UserRole.USER));
        this.userRepository.saveAndFlush(user);

        kafkaProducer.sendMessage(DtoMapper.mapUserEntityToUserRegisterV1(user));
    }

    @Override
    public String verify(UserLoginRequest userLoginRequest) {
        UserEntity user = this.getUserByUsername(userLoginRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + userLoginRequest.getUsername() + " not found"));

        if (!user.isApproved()) {
            throw new UnapprovedUserException("User is not approved yet.");
        }

        if (user.isBanned()) {
            throw new BannedUserException("Your account has been banned. Reason: " + user.getReasonForBan());
        }

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            String role = authentication.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElseThrow(() -> new IllegalStateException("User has no roles assigned"));

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
    public ChangeRoleUserResponse updateUserRole(UUID userId, RoleRequest roleRequest) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserRole userRole = Enum.valueOf(UserRole.class, roleRequest.getRole());
        Role role = this.userRoleRepository.findByRole(userRole);

        user.getPermissions().clear();

        user.setRole(role);
        userRepository.saveAndFlush(user);

        return DtoMapper.mapUserEntityToChangeRoleUserResponse(user);
    }

    @Override
    public List<BanUserResponse> getAllUsersForBan() {
        return userRepository.findAll()
                .stream()
                .map(DtoMapper::mapUserEntityToBanUserResponse)
                .toList();
    }

    @Override
    public BanUserResponse banUserAction(UUID id, BanUserReasonRequest banUserReasonRequest) {

        UserEntity user = this.userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User with ID " + id + " not found"));

        if (banUserReasonRequest.getAction().equals("ban")) {
            user.setBanned(true);
            user.setReasonForBan(banUserReasonRequest.getReason());
        } else {
            user.setBanned(false);
            user.setReasonForBan(null);
        }

        this.userRepository.saveAndFlush(user);

        return this.getUserForBan(id);
    }

    @Override
    public BanUserResponse getUserForBan(UUID id) {
        UserEntity user = this.userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User with ID " + id + " not found"));

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
    public ApproveUsersResponse approveUserAction(UUID id, ApproveUserReasonRequest reasonRequest) {

        UserEntity user = this.userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User with ID " + id + " not found"));
        if (reasonRequest.getAction().equals("approve")) {
            user.setApproved(true);
        } else {
            user.setApproved(false);
            user.setBanned(true);
            user.setReasonForBan(reasonRequest.getReason());
        }

        this.userRepository.saveAndFlush(user);

        return this.getUserForApprove(id);
    }

    @Override
    public ApproveUsersResponse getUserForApprove(UUID id) {
        UserEntity user = this.userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User with ID " + id + " not found"));

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
    public AdminPermissionsResponse updateAdminPermissions(UUID id, AdminPermissionsUpdateRequest updatePermission) {
        UserEntity admin = this.userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User with ID " + id + " not found"));

        admin.getPermissions().addAll(updatePermission.getPermissionsToAdd());
        admin.getPermissions().removeAll(updatePermission.getPermissionsToRemove());

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
    public ModeratorPermissionsResponse updateModerationPermissions(UUID id, ModeratorPermissionsUpdateRequest updatePermission) {
        UserEntity moderator = this.userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User with ID " + id + " not found"));

        moderator.getPermissions().addAll(updatePermission.getPermissionsToAdd());
        moderator.getPermissions().removeAll(updatePermission.getPermissionsToRemove());

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
    public boolean addFriendByUsername(FriendRequest friendRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity friend = this.getUserByUsername(friendRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + friendRequest.getUsername() + " not found"));

        boolean isBlockedByMe = this.isUserBlockedByMe(username, friendRequest.getUsername());
        boolean isBlockedBy = this.isUserBlocked(username, friendRequest.getUsername());

        if (isBlockedByMe || isBlockedBy) {
            throw new BlockedUserException("One of the users are block other cannot add as friend");
        }

        user.getSendFriendRequest().add(friend);
        friend.getReceiveFriendRequest().add(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);

        return true;
    }

    @Override
    public boolean followUserByUsername(FollowerUserRequest followerUserRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity follower = this.getUserByUsername(followerUserRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + followerUserRequest.getUsername() + " not found"));

        boolean isBlockedByMe = this.isUserBlockedByMe(username, followerUserRequest.getUsername());
        boolean isBlockedBy = this.isUserBlocked(username, followerUserRequest.getUsername());

        if (isBlockedByMe || isBlockedBy) {
            throw new BlockedUserException("One of the users are block other cannot follow");
        }

        user.getFollowing().add(follower);
        follower.getFollowers().add(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(follower);

        return true;
    }

    @Override
    public boolean acceptFriendRequest(FriendRequest friendRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity friend = this.getUserByUsername(friendRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + friendRequest.getUsername() + " not found"));

        boolean isBlockedByMe = this.isUserBlockedByMe(username, friendRequest.getUsername());
        boolean isBlockedBy = this.isUserBlocked(username, friendRequest.getUsername());

        if (isBlockedByMe || isBlockedBy) {
            throw new BlockedUserException("One of the users are block other cannot accept friend request");
        }

        user.getFriends().add(friend);
        friend.getFriends().add(user);

        user.getReceiveFriendRequest().remove(friend);
        friend.getSendFriendRequest().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);

        return true;
    }

    @Override
    public boolean rejectFriendRequest(FriendRequest friendRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity friend = this.getUserByUsername(friendRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + friendRequest.getUsername() + " not found"));

        user.getReceiveFriendRequest().remove(friend);
        friend.getSendFriendRequest().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);

        return true;
    }

    @Override
    public boolean isValidUser(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    @Override
    public boolean removeFriendByUsername(FriendRequest friendRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity friend = this.getUserByUsername(friendRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + friendRequest.getUsername() + " not found"));

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);

        return true;
    }

    @Override
    public boolean cancelFriendRequestByUsername(FriendRequest friendRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity friend = this.getUserByUsername(friendRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + friendRequest.getUsername() + " not found"));

        user.getSendFriendRequest().remove(friend);
        friend.getReceiveFriendRequest().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);

        return true;
    }

    @Override
    public boolean unfollowUserByUsername(FollowerUserRequest followerUserRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity follower = this.getUserByUsername(followerUserRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + followerUserRequest.getUsername() + " not found"));

        user.getFollowing().remove(follower);
        follower.getFollowers().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(follower);

        return true;
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
    public boolean removeFollowerByUsername(FollowerUserRequest followerUserRequest, String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity follower = this.getUserByUsername(followerUserRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException("User with username " + followerUserRequest.getUsername() + " not found"));

        user.getFollowers().remove(follower);
        follower.getFollowing().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(follower);

        return true;
    }

    @Override
    public boolean blockUser(String username, String blockedUsername) {
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

        return true;
    }

    @Override
    public boolean unblockUser(String username, String blockedUsername) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity blockedUser = this.getUserByUsername(blockedUsername).orElseThrow(() ->
                new UserNotFoundException("User with username " + blockedUsername + " not found"));

        user.getBlockedUsers().remove(blockedUser);
        this.userRepository.saveAndFlush(user);

        return true;
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
    public boolean isUserBlockedByMe(String username, String blockerUsername) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        UserEntity blocker = this.getUserByUsername(blockerUsername).orElseThrow(() ->
                new UserNotFoundException("User with username " + blockerUsername + " not found"));

        return user.getBlockedUsers().contains(blocker);
    }

    @Override
    public ContactUserResponse getUserDetails(String username) {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        return DtoMapper.mapUserEntityToContactUserResponse(user);
    }

    @Override
    public UserInformationForPictureResponse getUserById(UUID userId) {
        UserEntity user = this.userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User with ID " + userId + " not found"));

        return DtoMapper.mapUserEntityToUserInformationForPictureResponse(user);
    }

    @Override
    public Map<String, String> handleValidationErrors(BindingResult bindingResult) {
        Map<String, String> errorResponse = new HashMap<>();
        bindingResult.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorResponse.put(fieldName, errorMessage);
        });

        return errorResponse;
    }

    @Override
    public boolean editUserProfilePicture(MultipartFile file, String username) throws IOException {
        UserEntity user = this.getUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User with username " + username + " not found"));

        Map<String, Object> uploadResult = this.cloudinaryService.uploadImage(file);
        String pictureFilePath = (String) uploadResult.get("secure_url");

        user.setProfilePicturePath(pictureFilePath);

        this.userRepository.saveAndFlush(user);

        return true;
    }
}
