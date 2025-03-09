package bg.photographyjava.user.service.impl;

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
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RankRepository userRankRepository;
    private final RoleRepository userRoleRepository;
    private final CountryRepository countryRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, RankRepository userRankRepository, RoleRepository userRoleRepository, CountryRepository countryRepository, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRankRepository = userRankRepository;
        this.userRoleRepository = userRoleRepository;
        this.countryRepository = countryRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
    public void registerUser(UserRegisterDTO userRegisterDTO) {
        UserEntity user = this.modelMapper.map(userRegisterDTO, UserEntity.class);
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setCountry(this.countryRepository.findByName(CountryEnum.fromString(userRegisterDTO.getCountry())));
        user.setGender(GenderEnum.fromString(userRegisterDTO.getGender()));
        user.setRank(this.userRankRepository.findByRank(UserRank.BEGINNER));
        user.setRole(this.userRoleRepository.findByRole(UserRole.USER));
        user.setApproved(false);
        user.setRealName("Anonymous");
        user.setProfilePicturePath("https://res.cloudinary.com/dkyp0c0lz/image/upload/v1737304170/male-profile-picture_rltohq.avif");
        user.setPoints(0);
        user.setBanned(false);
        user.setApproved(false);
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

            UserEntity user = this.userRepository.findByUsername(userLoginRequest.getUsername()).get();
            List<UserPermission> userPermissions = user.getPermissions().stream().toList();

            return jwtService.generateToken(userLoginRequest.getUsername(), role, userPermissions, user.getId());
        }
        return "Failed";
    }

    @Override
    public UserProfileDTO getProfileDetails(String username) {
        UserEntity user = this.getUserByUsername(username).get();
        UserProfileDTO userProfileDTO = this.modelMapper.map(user, UserProfileDTO.class);
        userProfileDTO.setCountry(user.getCountry().getName().getCountryName());
        userProfileDTO.setRank(user.getRank().getRank().toString());
        userProfileDTO.setPoints(user.getPoints());

        LocalDate birthDate = user.getBirthDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        userProfileDTO.setAge(age);

        return userProfileDTO;
    }

    @Override
    public UserEditProfileDTO getProfileEditDetails(String username) {
        UserEntity user = this.getUserByUsername(username).get();

        return this.modelMapper.map(user, UserEditProfileDTO.class);
    }

    @Override
    public void editUserDetails(String username, UserEditProfileDTO userEditProfileDTO) {
        UserEntity user = this.getUserByUsername(username).get();
        user.setRealName(userEditProfileDTO.getRealName());
        user.setCity(userEditProfileDTO.getCity());
        user.setBirthDate(userEditProfileDTO.getBirthDate());

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public UserChangeUsernameDTO getUserUsernameDetails(String username) {
        UserEntity user = this.getUserByUsername(username).get();
        UserChangeUsernameDTO userChangeUsernameDTO = new UserChangeUsernameDTO();
        userChangeUsernameDTO.setOldUsername(user.getUsername());

        return userChangeUsernameDTO;
    }

    @Override
    public UserChangeEmailDTO getUserEmailDetails(String username) {
        UserEntity user = this.getUserByUsername(username).get();

        UserChangeEmailDTO userChangeEmailDTO = new UserChangeEmailDTO();
        userChangeEmailDTO.setOldEmail(user.getEmail());
        return userChangeEmailDTO;
    }

    @Override
    public void editUserUsernameDetails(String username, UserChangeUsernameDTO userChangeUsernameDTO) {
        UserEntity user = this.getUserByUsername(username).get();
        user.setUsername(userChangeUsernameDTO.getNewUsername());

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public void editUserEmailDetails(String username, UserChangeEmailDTO userChangeEmailDTO) {
        UserEntity user = this.getUserByUsername(username).get();
        user.setEmail(userChangeEmailDTO.getNewEmail());

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public void updatePassword(String username, String encodedNewPassword) {
        UserEntity user = this.getUserByUsername(username).get();
        user.setPassword(encodedNewPassword);

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public List<ChangeRoleUserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user -> new ChangeRoleUserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().getRole().name()
        )).toList();
    }

    @Override
    public void updateUserRole(UUID userId, String roleToChange, String username) {
        UserEntity admin = this.getUserByUsername(username).get();
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
        return userRepository.findAll().stream().map(user -> new BanUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isBanned(),
                user.getReasonForBan()
        )).toList();
    }

    @Override
    public void banUserAction(UUID id, BanUserReasonRequest reasonDTO, String username) {
        UserEntity admin = this.userRepository.findByUsername(username).get();
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

        return this.modelMapper.map(user, BanUserResponse.class);
    }

    @Override
    public List<ApproveUsersResponse> getAllUsersForApprove() {
        return userRepository.findAll().stream().filter(userEntity -> !userEntity.isApproved() && !userEntity.isBanned()).map(user -> new ApproveUsersResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        )).toList();
    }

    @Override
    public void approveUserAction(UUID id, ApproveUserReasonRequest reasonDTO, String username) {
        UserEntity admin = this.userRepository.findByUsername(username).get();
        UserEntity user = this.userRepository.findById(id).get();
        if (reasonDTO.getAction().equals("approve")) {
            user.setApproved(true);
        } else {
            user.setApproved(false);
            user.setBanned(true);
            user.setReasonForBan(reasonDTO.getReason());
        }

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public ApproveUsersResponse getUserForApprove(UUID id) {
        UserEntity user = this.userRepository.findById(id).get();

        return this.modelMapper.map(user, ApproveUsersResponse.class);
    }

    @Override
    public List<AdminPermissionsResponse> getAllAdminsWithPermissions() {
        return this.userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && user.getRole().getRole() == UserRole.ADMIN)
                .map(admin -> new AdminPermissionsResponse(
                        admin.getId(),
                        admin.getUsername(),
                        admin.getPermissions()
                ))
                .toList();
    }

    @Override
    public AdminPermissionsResponse updateAdminPermissions(UUID id, Set<UserPermission> permissionsToAdd, Set<UserPermission> permissionsToRemove, String username) {
        UserEntity superAdmin = this.userRepository.findByUsername(username).get();
        UserEntity admin = this.userRepository.findById(id).get();

        admin.getPermissions().addAll(permissionsToAdd);
        admin.getPermissions().removeAll(permissionsToRemove);

        this.userRepository.saveAndFlush(admin);

        return this.modelMapper.map(admin, AdminPermissionsResponse.class);
    }

    @Override
    public List<ModeratorPermissionsResponse> getAllModeratorsWithPermissions() {
        return this.userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && user.getRole().getRole() == UserRole.MODERATOR)
                .map(admin -> new ModeratorPermissionsResponse(
                        admin.getId(),
                        admin.getUsername(),
                        admin.getPermissions()
                ))
                .toList();
    }

    @Override
    public ModeratorPermissionsResponse updateModerationPermissions(UUID id, Set<UserPermission> permissionsToAdd, Set<UserPermission> permissionsToRemove, String username) {
        UserEntity superAdmin = this.userRepository.findByUsername(username).get();
        UserEntity moderator = this.userRepository.findById(id).get();

        moderator.getPermissions().addAll(permissionsToAdd);
        moderator.getPermissions().removeAll(permissionsToRemove);

        this.userRepository.saveAndFlush(moderator);

        return this.modelMapper.map(moderator, ModeratorPermissionsResponse.class);
    }

    @Override
    public List<UserEntity> getAllUsersForCountries() {
        return this.userRepository.findAll();
    }

    @Override
    public List<UserPermission> getCurrentAdminPermissions(String username) {
        UserEntity user = this.getUserByUsername(username).get();
        return user.getPermissions().stream().toList();
    }

    @Override
    public void addFriendByUsername(AddFriendDTO addFriendDTO, String username) {
        UserEntity user = this.getUserByUsername(username).get();
        UserEntity friend = this.getUserByUsername(addFriendDTO.getUsername()).get();

        user.getSendFriendRequest().add(friend);
        friend.getReceiveFriendRequest().add(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);
    }

    @Override
    public void followUserByUsername(FollowerUserRequest followerUserRequest, String username) {
        UserEntity user = this.getUserByUsername(username).get();
        UserEntity follower = this.getUserByUsername(followerUserRequest.getUsername()).get();

        user.getFollowing().add(follower);
        follower.getFollowers().add(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(follower);
    }

    @Override
    public void acceptFriendRequest(AddFriendDTO addFriendDTO, String username) {
        UserEntity user = this.getUserByUsername(username).get();
        UserEntity friend = this.getUserByUsername(addFriendDTO.getUsername()).get();

        user.getFriends().add(friend);
        friend.getFriends().add(user);

        user.getReceiveFriendRequest().remove(friend);
        friend.getSendFriendRequest().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);
    }

    @Override
    public void rejectFriendRequest(AddFriendDTO addFriendDTO, String username) {
        UserEntity user = this.getUserByUsername(username).get();
        UserEntity friend = this.getUserByUsername(addFriendDTO.getUsername()).get();

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
    public void removeFriendByUsername(AddFriendDTO addFriendDTO, String username) {
        UserEntity user = this.getUserByUsername(username).get();
        UserEntity friend = this.getUserByUsername(addFriendDTO.getUsername()).get();

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);
    }

    @Override
    public void cancelFriendRequestByUsername(AddFriendDTO addFriendDTO, String username) {
        UserEntity user = this.getUserByUsername(username).get();
        UserEntity friend = this.getUserByUsername(addFriendDTO.getUsername()).get();

        user.getSendFriendRequest().remove(friend);
        friend.getReceiveFriendRequest().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(friend);
    }

    @Override
    public void unfollowUserByUsername(FollowerUserRequest followerUserRequest, String username) {
        UserEntity user = this.getUserByUsername(username).get();
        UserEntity follower = this.getUserByUsername(followerUserRequest.getUsername()).get();

        user.getFollowing().remove(follower);
        follower.getFollowers().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(follower);
    }

    @Override
    public boolean areFriends(String username, String targetUsername) {
        UserEntity user = userRepository.findByUsername(username).get();
        UserEntity targetUser = userRepository.findByUsername(targetUsername).get();

        return user.getFriends().contains(targetUser);
    }

    @Override
    public boolean hasSentFriendRequest(String username, String targetUsername) {
        UserEntity user = userRepository.findByUsername(username).get();
        UserEntity targetUser = userRepository.findByUsername(targetUsername).get();

        return user.getSendFriendRequest().contains(targetUser);
    }

    @Override
    public boolean isFollowing(String username, String targetUsername) {
        UserEntity user = userRepository.findByUsername(username).get();
        UserEntity targetUser = userRepository.findByUsername(targetUsername).get();

        return targetUser.getFollowers().contains(user);
    }

    @Override
    public Set<FriendsResponse> getSentFriendRequests(String username) {
        UserEntity user = userRepository.findByUsername(username).get();
        Set<UserEntity> friendRequest = user.getSendFriendRequest();
        return friendRequest.stream()
                .map(request -> modelMapper.map(request, FriendsResponse.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FriendsResponse> getReceiveFriendRequests(String username) {
        UserEntity user = userRepository.findByUsername(username).get();
        Set<UserEntity> receiveRequest = user.getReceiveFriendRequest();
        return receiveRequest.stream()
                .map(request -> modelMapper.map(request, FriendsResponse.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FollowersResponse> getAllFollowers(String username) {
        UserEntity user = userRepository.findByUsername(username).get();
        Set<UserEntity> followers = user.getFollowers();
        return followers.stream()
                .map(request -> modelMapper.map(request, FollowersResponse.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FollowersResponse> getAllFollowings(String username) {
        UserEntity user = userRepository.findByUsername(username).get();
        Set<UserEntity> followings = user.getFollowing();
        return followings.stream()
                .map(request -> modelMapper.map(request, FollowersResponse.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FriendsResponse> getAllFriends(String username) {
        UserEntity user = userRepository.findByUsername(username).get();
        Set<UserEntity> friends = user.getFriends();
        return friends.stream()
                .map(request -> modelMapper.map(request, FriendsResponse.class))
                .collect(Collectors.toSet());
    }

    @Override
    public void removeFollowerByUsername(FollowerUserRequest followerUserRequest, String username) {
        UserEntity user = this.getUserByUsername(username).get();
        UserEntity follower = this.getUserByUsername(followerUserRequest.getUsername()).get();

        user.getFollowers().remove(follower);
        follower.getFollowing().remove(user);

        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(follower);
    }

    @Override
    public void blockUser(String username, String blockedUsername) {
        UserEntity user = this.getUserByUsername(username).get();
        UserEntity blockedUser = this.getUserByUsername(blockedUsername).get();

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
        UserEntity user = this.getUserByUsername(username).get();
        UserEntity blockedUser = this.getUserByUsername(blockedUsername).get();

        user.getBlockedUsers().remove(blockedUser);
        this.userRepository.saveAndFlush(user);
    }

    @Override
    public Set<BlockedUserResponse> getBlockedUsers(String username) {
        UserEntity user = this.getUserByUsername(username).get();
        Set<UserEntity> blockedUsers = user.getBlockedUsers();

        return blockedUsers.stream()
                .map(request -> this.modelMapper.map(request, BlockedUserResponse.class))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isUserBlocked(String username, String blockedUsername) {
        UserEntity user = this.userRepository.findByUsername(username).get();
        UserEntity targetUser = this.userRepository.findByUsername(blockedUsername).get();

        return targetUser.getBlockedUsers().contains(user);
    }

    @Override
    public ContactUserResponse getUserDetails(String username) {
        UserEntity user = this.getUserByUsername(username).get();
        return this.modelMapper.map(user, ContactUserResponse.class);
    }
}
