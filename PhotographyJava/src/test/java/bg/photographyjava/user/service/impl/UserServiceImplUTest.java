package bg.photographyjava.user.service.impl;

import bg.photographyjava.exception.InvalidPasswordException;
import bg.photographyjava.exception.OldEmailMismatchException;
import bg.photographyjava.exception.UserNotFoundException;
import bg.photographyjava.shared.service.CloudinaryService;
import bg.photographyjava.shared.service.impl.KafkaProducer;
import bg.photographyjava.user.model.Country;
import bg.photographyjava.user.model.Role;
import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.user.property.enums.CountryEnum;
import bg.photographyjava.user.property.enums.UserPermission;
import bg.photographyjava.user.property.enums.UserRole;
import bg.photographyjava.user.repository.CountryRepository;
import bg.photographyjava.user.repository.RoleRepository;
import bg.photographyjava.user.repository.UserRepository;
import bg.photographyjava.web.dto.*;
import bg.photographyjava.web.filter.JWTService;
import bg.photographyjava.web.mapper.DtoMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository userRoleRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUser;
    private UUID userId;
    private Role testRole;

    private MockedStatic<DtoMapper> mockedMapper;

    @BeforeEach
    public void setUp() {
        testUser = new UserEntity();
        testUser.setUsername("testUser");
        testUser.setEmail("testUser@example.com");
        testUser.setPassword("password123");
        testRole = new Role();
        testRole.setRole(UserRole.USER);
        testUser.setRole(testRole);
        Country country = new Country();
        country.setName(CountryEnum.BULGARIA);
        this.countryRepository.saveAndFlush(country);
        testUser.setCountry(country);
        userId = UUID.randomUUID();

        mockedMapper = Mockito.mockStatic(DtoMapper.class);
    }

    @AfterEach
    void tearDown() {
        mockedMapper.close();
    }

    @Test
    void testGetUserByEmail() {
        String email = "testUser@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        Optional<UserEntity> result = userService.getUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void testRegisterUser_Success() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testUser");
        request.setEmail("testUser@example.com");
        request.setPassword("password123");
        request.setCountry("BULGARIA");

        UserEntity mappedUser = new UserEntity();
        mappedUser.setUsername(request.getUsername());
        mappedUser.setEmail(request.getEmail());

        Country country = new Country();
        country.setName(CountryEnum.BULGARIA);

        Role role = new Role();
        role.setRole(UserRole.USER);

        mockedMapper.when(() -> DtoMapper.mapUserRegisterRequestToUserEntity(request)).thenReturn(mappedUser);
        mockedMapper.when(() -> DtoMapper.mapUserEntityToUserRegisterV1(any())).thenReturn(new UserRegisterV1());

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(countryRepository.findByName(CountryEnum.BULGARIA)).thenReturn(country);
        when(userRoleRepository.findByRole(UserRole.USER)).thenReturn(role);

        userService.registerUser(request);

        assertEquals("encodedPassword", mappedUser.getPassword());
        assertEquals(country, mappedUser.getCountry());
        assertEquals(role, mappedUser.getRole());
        verify(userRepository, times(1)).saveAndFlush(mappedUser);
        verify(kafkaProducer, times(1)).sendMessage(any(UserRegisterV1.class));
    }

    @Test
    void testEditUserDetails_UserNotFound() {
        String username = "testUser";
        UserEditProfileRequest request = new UserEditProfileRequest();
        request.setRealName("New Real Name");

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.editUserDetails(username, request);
        });
    }

    @Test
    void testGetProfileDetails_Success() {
        String username = "testUser";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail("testUser@example.com");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserProfileResponse mockResponse = new UserProfileResponse();
        when(DtoMapper.mapUserEntityToUserProfileResponse(user)).thenReturn(mockResponse);

        UserProfileResponse response = userService.getProfileDetails(username);

        assertNotNull(response);
        assertEquals(mockResponse, response);
    }


    @Test
    void testUpdatePassword() {
        UserChangePasswordRequest changePasswordRequest = new UserChangePasswordRequest();
        changePasswordRequest.setOldPassword("password123");
        changePasswordRequest.setNewPassword("newPassword123");

        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(changePasswordRequest.getOldPassword(), testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(changePasswordRequest.getNewPassword())).thenReturn("encodedNewPassword123");

        userService.updatePassword(testUser.getUsername(), changePasswordRequest);

        verify(userRepository, times(1)).saveAndFlush(testUser);

        assertEquals("encodedNewPassword123", testUser.getPassword());
    }

    @Test
    void testGetProfileEditDetails_Success() {
        String username = "testUser";
        String fullName = "testUser";
        UserEntity user = new UserEntity();
        user.setRealName(fullName);
        user.setCity("Balgoevgrad");

        UserEditProfileResponse response = new UserEditProfileResponse(); // Simulated response
        response.setRealName(user.getRealName());
        response.setCity(user.getCity());

            mockedMapper.when(() -> DtoMapper.mapUserEntityToUserEditProfileResponse(user)).thenReturn(response);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            UserEditProfileResponse actualResponse = userService.getProfileEditDetails(username);

            assertNotNull(actualResponse);
            assertEquals(user.getRealName(), actualResponse.getRealName());
            assertEquals(user.getCity(), actualResponse.getCity());
    }

    @Test
    void testGetProfileEditDetails_UserNotFound() {
        String username = "unknownUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getProfileEditDetails(username));
    }


    @Test
    void testGetUserUsernameDetails_Success() {
        String username = "testUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        UserChangeUsernameResponse expectedResponse = new UserChangeUsernameResponse();
        expectedResponse.setOldUsername(testUser.getUsername());

        mockedMapper.when(() -> DtoMapper.mapUserEntityToUserChangeUsernameResponse(testUser))
                .thenReturn(expectedResponse);

        UserChangeUsernameResponse response = userService.getUserUsernameDetails(username);

        assertNotNull(response);
        assertEquals(testUser.getUsername(), response.getOldUsername());

    }

    @Test
    void testGetUserUsernameDetails_UserNotFound() {
        String username = "unknownUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserUsernameDetails(username));
    }

    @Test
    void testGetUserEmailDetails_Success() {
        String username = "testUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        UserChangeEmailResponse expectedResponse = new UserChangeEmailResponse();
        expectedResponse.setOldEmail(testUser.getEmail());

        mockedMapper.when(() -> DtoMapper.mapUserEntityToUserChangeEmailResponse(testUser))
                .thenReturn(expectedResponse);

        UserChangeEmailResponse response = userService.getUserEmailDetails(username);

        assertNotNull(response);
        assertEquals(testUser.getEmail(), response.getOldEmail());
    }

    @Test
    void testGetUserEmailDetails_UserNotFound() {
        String username = "unknownUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserEmailDetails(username));
    }

    @Test
    void testEditUserUsernameDetails_InvalidPassword() {
        String username = "testUser";
        UserChangeUsernameRequest request = new UserChangeUsernameRequest();
        request.setOldUsername("testUser");
        request.setNewUsername("newUsername");
        request.setPassword("wrongPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            userService.editUserUsernameDetails(username, request);
        });

        assertEquals("Invalid current password", exception.getMessage());
    }

    @Test
    void testEditUserUsernameDetails_OldUsernameMismatch() {
        String username = "testUser";
        UserChangeUsernameRequest request = new UserChangeUsernameRequest();
        request.setOldUsername("wrongOldUsername");
        request.setNewUsername("newUsername");
        request.setPassword("password123");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            userService.editUserUsernameDetails(username, request);
        });

        assertEquals("Invalid current password", exception.getMessage());
    }

    @Test
    void testEditUserUsernameDetails_Success() {
        String username = "testUser";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail("testUser@example.com");

        UserChangeUsernameResponse response = new UserChangeUsernameResponse();
        response.setOldUsername(user.getUsername());

            mockedMapper.when(() -> DtoMapper.mapUserEntityToUserChangeUsernameResponse(user)).thenReturn(response);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            UserChangeUsernameResponse actualResponse = userService.getUserUsernameDetails(username);

            assertNotNull(actualResponse);
            assertEquals(user.getUsername(), actualResponse.getOldUsername());

    }

    @Test
    void testEditUserEmailDetails_InvalidPassword() {
        String username = "testUser";
        UserChangeEmailRequest request = new UserChangeEmailRequest();
        request.setOldEmail("testUser@example.com");
        request.setNewEmail("newEmail@example.com");
        request.setPassword("wrongPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            userService.editUserEmailDetails(username, request);
        });

        assertEquals("Invalid current password", exception.getMessage());
    }

    @Test
    void testEditUserEmailDetails_OldEmailMismatch() {
        String username = "testUser";
        UserChangeEmailRequest request = new UserChangeEmailRequest();
        request.setOldEmail("wrongOldEmail@example.com");
        request.setNewEmail("newEmail@example.com");
        request.setPassword("password123");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(true);

        OldEmailMismatchException exception = assertThrows(OldEmailMismatchException.class, () -> {
            userService.editUserEmailDetails(username, request);
        });

        assertEquals("Old email does not match the authenticated user email.", exception.getMessage());
    }

    @Test
    void testEditUserEmailDetails_Success() {
        String username = "testUser";
        UserChangeEmailRequest request = new UserChangeEmailRequest();
        request.setOldEmail("testUser@example.com");
        request.setNewEmail("newEmail@example.com");
        request.setPassword("password123");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(true);

        mockedMapper.when(() -> DtoMapper.mapUserChangeEmailRequestToUserEntity(testUser, request))
                .thenAnswer(invocation -> {
                    testUser.setEmail(request.getNewEmail());
                    return null;
                });

        userService.editUserEmailDetails(username, request);

        verify(userRepository, times(1)).saveAndFlush(testUser);
        assertEquals("newEmail@example.com", testUser.getEmail());

    }

    @Test
    void testGetAllUsers() {
        List<UserEntity> mockUsers = List.of(testUser);
        when(userRepository.findAll()).thenReturn(mockUsers);

        ChangeRoleUserResponse mockResponse = new ChangeRoleUserResponse();
        mockedMapper.when(() -> DtoMapper.mapUserEntityToChangeRoleUserResponse(testUser)).thenReturn(mockResponse);

        List<ChangeRoleUserResponse> response = userService.getAllUsers();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertSame(mockResponse, response.get(0));
    }

    @Test
    void testUpdateUserRole() {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setRole("USER");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRoleRepository.findByRole(UserRole.USER)).thenReturn(testRole);
        mockedMapper.when(() -> DtoMapper.mapUserEntityToChangeRoleUserResponse(testUser)).thenReturn(new ChangeRoleUserResponse());

        ChangeRoleUserResponse response = userService.updateUserRole(userId, roleRequest);

        assertNotNull(response);
        assertEquals(testRole, testUser.getRole());
    }

    @Test
    void testGetAllUsersForBan() {

        when(userRepository.findAll()).thenReturn(List.of(testUser));
        mockedMapper.when(() -> DtoMapper.mapUserEntityToBanUserResponse(testUser)).thenReturn(new BanUserResponse());

        List<BanUserResponse> response = userService.getAllUsersForBan();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void testBanUserAction() {
        BanUserReasonRequest banUserReasonRequest = new BanUserReasonRequest();
        banUserReasonRequest.setAction("ban");
        banUserReasonRequest.setReason("Spamming");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        mockedMapper.when(() -> DtoMapper.mapUserEntityToBanUserResponse(testUser)).thenReturn(new BanUserResponse());

        BanUserResponse response = userService.banUserAction(userId, banUserReasonRequest);

        assertTrue(testUser.isBanned());
        assertEquals("Spamming", testUser.getReasonForBan());
    }

    @Test
    void testGetUserForBan() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        mockedMapper.when(() -> DtoMapper.mapUserEntityToBanUserResponse(testUser)).thenReturn(new BanUserResponse());

        BanUserResponse response = userService.getUserForBan(userId);

        assertNotNull(response);
    }

    @Test
    void testGetAllUsersForApprove() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        mockedMapper.when(() -> DtoMapper.mapUserEntityToApproveUsersResponse(testUser)).thenReturn(new ApproveUsersResponse());

        List<ApproveUsersResponse> response = userService.getAllUsersForApprove();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void testApproveUserAction() {
        ApproveUserReasonRequest reasonRequest = new ApproveUserReasonRequest();
        reasonRequest.setAction("approve");
        reasonRequest.setReason("Valid reason");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        mockedMapper.when(() -> DtoMapper.mapUserEntityToApproveUsersResponse(testUser)).thenReturn(new ApproveUsersResponse());

        ApproveUsersResponse response = userService.approveUserAction(userId, reasonRequest);

        assertTrue(testUser.isApproved());
        assertNull(testUser.getReasonForBan());
    }

    @Test
    void testGetUserForApprove() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        mockedMapper.when(() -> DtoMapper.mapUserEntityToApproveUsersResponse(testUser)).thenReturn(new ApproveUsersResponse());

        ApproveUsersResponse response = userService.getUserForApprove(userId);

        assertNotNull(response);
    }

    @Test
    void testGetAllAdminsWithPermissions() {
        UserEntity admin1 = new UserEntity();
        admin1.setRole(new Role(UserRole.ADMIN));

        UserEntity admin2 = new UserEntity();
        admin2.setRole(new Role(UserRole.ADMIN));

        UserEntity nonAdmin = new UserEntity();
        nonAdmin.setRole(new Role(UserRole.USER));

        when(userRepository.findAll()).thenReturn(List.of(admin1, admin2, nonAdmin));

        List<AdminPermissionsResponse> response = userService.getAllAdminsWithPermissions();

        assertNotNull(response);
        assertEquals(2, response.size());
    }



    @Test
    void testGetAllModeratorsWithPermissions() {
        UserEntity moderator1 = new UserEntity();
        moderator1.setRole(new Role(UserRole.MODERATOR));

        UserEntity moderator2 = new UserEntity();
        moderator2.setRole(new Role(UserRole.MODERATOR));

        UserEntity nonModerator = new UserEntity();
        nonModerator.setRole(new Role(UserRole.USER));

        when(userRepository.findAll()).thenReturn(List.of(moderator1, moderator2, nonModerator));

        List<ModeratorPermissionsResponse> response = userService.getAllModeratorsWithPermissions();

        assertNotNull(response);
        assertEquals(2, response.size());
    }



    @Test
    void testFollowUserByUsername() {
        String username = "user1";
        FollowerUserRequest followerUserRequest = new FollowerUserRequest();
        followerUserRequest.setUsername("user2");

        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity follower = new UserEntity();
        follower.setUsername("user2");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(followerUserRequest.getUsername())).thenReturn(Optional.of(follower));

        boolean result = userService.followUserByUsername(followerUserRequest, username);

        assertTrue(result);
        assertTrue(user.getFollowing().contains(follower));
        assertTrue(follower.getFollowers().contains(user));
        verify(userRepository, times(1)).saveAndFlush(user);
        verify(userRepository, times(1)).saveAndFlush(follower);
    }


    @Test
    void testAcceptFriendRequest() {
        String username = "user1";
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUsername("user2");

        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity friend = new UserEntity();
        friend.setUsername("user2");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(friendRequest.getUsername())).thenReturn(Optional.of(friend));

        boolean result = userService.acceptFriendRequest(friendRequest, username);

        assertTrue(result);
        assertTrue(user.getFriends().contains(friend));
        assertTrue(friend.getFriends().contains(user));
        verify(userRepository, times(1)).saveAndFlush(user);
        verify(userRepository, times(1)).saveAndFlush(friend);
    }

    @Test
    void testEditUserProfilePicture_Success() throws IOException {
        String username = "testUser";
        UserEntity testUser = new UserEntity();
        testUser.setUsername(username);

        MultipartFile mockFile = mock(MultipartFile.class);

        Map<String, Object> mockUploadResult = new HashMap<>();
        mockUploadResult.put("secure_url", "https://example.com/image.jpg");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(cloudinaryService.uploadImage(any(MultipartFile.class))).thenReturn(mockUploadResult);

        boolean result = userService.editUserProfilePicture(mockFile, username);

        assertTrue(result);
        assertEquals("https://example.com/image.jpg", testUser.getProfilePicturePath());

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());

        UserEntity savedUser = userCaptor.getValue();
        assertEquals("https://example.com/image.jpg", savedUser.getProfilePicturePath());
    }

    @Test
    void testGetUserById_Success() {
        UUID userId = UUID.randomUUID();
        UserEntity testUser = new UserEntity();
        testUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(DtoMapper.mapUserEntityToUserInformationForPictureResponse(testUser))
                .thenReturn(new UserInformationForPictureResponse());

        UserInformationForPictureResponse response = userService.getUserById(userId);

        assertNotNull(response);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserDetails_Success() {
        String username = "testUser";
        UserEntity testUser = new UserEntity();
        testUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(DtoMapper.mapUserEntityToContactUserResponse(testUser))
                .thenReturn(new ContactUserResponse());

        ContactUserResponse response = userService.getUserDetails(username);

        assertNotNull(response);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testIsUserBlocked() {
        String username = "user1";
        String blockedUsername = "user2";

        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity blockedUser = new UserEntity();
        blockedUser.setUsername(blockedUsername);
        blockedUser.getBlockedUsers().add(user);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(blockedUsername)).thenReturn(Optional.of(blockedUser));

        boolean isBlocked = userService.isUserBlocked(username, blockedUsername);

        assertTrue(isBlocked);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(blockedUsername);
    }

    @Test
    void testGetBlockedUsers() {
        String username = "user1";
        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity blockedUser1 = new UserEntity();
        blockedUser1.setUsername("blockedUser1");

        UserEntity blockedUser2 = new UserEntity();
        blockedUser2.setUsername("blockedUser2");

        user.getBlockedUsers().add(blockedUser1);
        user.getBlockedUsers().add(blockedUser2);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(DtoMapper.mapUserEntityToBlockedUserResponse(blockedUser1)).thenReturn(new BlockedUserResponse());
        when(DtoMapper.mapUserEntityToBlockedUserResponse(blockedUser2)).thenReturn(new BlockedUserResponse());

        Set<BlockedUserResponse> blockedUsers = userService.getBlockedUsers(username);

        assertNotNull(blockedUsers);
        assertEquals(2, blockedUsers.size());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testUnblockUser() {
        String username = "user1";
        String blockedUsername = "blockedUser";

        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity blockedUser = new UserEntity();
        blockedUser.setUsername(blockedUsername);

        user.getBlockedUsers().add(blockedUser);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(blockedUsername)).thenReturn(Optional.of(blockedUser));

        boolean result = userService.unblockUser(username, blockedUsername);

        assertTrue(result);
        assertFalse(user.getBlockedUsers().contains(blockedUser));
        verify(userRepository, times(1)).saveAndFlush(user);
    }

    @Test
    void testBlockUser() {
        String username = "user1";
        String blockedUsername = "blockedUser";

        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity blockedUser = new UserEntity();
        blockedUser.setUsername(blockedUsername);

        user.getFriends().add(blockedUser);
        user.getReceiveFriendRequest().add(blockedUser);
        user.getSendFriendRequest().add(blockedUser);
        user.getFollowing().add(blockedUser);
        user.getFollowers().add(blockedUser);

        blockedUser.getFriends().add(user);
        blockedUser.getReceiveFriendRequest().add(user);
        blockedUser.getSendFriendRequest().add(user);
        blockedUser.getFollowing().add(user);
        blockedUser.getFollowers().add(user);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(blockedUsername)).thenReturn(Optional.of(blockedUser));

        boolean result = userService.blockUser(username, blockedUsername);

        assertTrue(result);
        assertTrue(user.getBlockedUsers().contains(blockedUser));
        assertFalse(user.getFriends().contains(blockedUser));
        assertFalse(user.getReceiveFriendRequest().contains(blockedUser));
        assertFalse(user.getSendFriendRequest().contains(blockedUser));
        assertFalse(user.getFollowing().contains(blockedUser));
        assertFalse(user.getFollowers().contains(blockedUser));

        assertFalse(blockedUser.getFriends().contains(user));
        assertFalse(blockedUser.getReceiveFriendRequest().contains(user));
        assertFalse(blockedUser.getSendFriendRequest().contains(user));
        assertFalse(blockedUser.getFollowing().contains(user));
        assertFalse(blockedUser.getFollowers().contains(user));

        verify(userRepository, times(1)).saveAndFlush(user);
        verify(userRepository, times(1)).saveAndFlush(blockedUser);
    }

    @Test
    void testRemoveFollowerByUsername() {
        String username = "user1";
        String followerUsername = "followerUser";

        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity follower = new UserEntity();
        follower.setUsername(followerUsername);

        user.getFollowers().add(follower);
        follower.getFollowing().add(user);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(followerUsername)).thenReturn(Optional.of(follower));

        FollowerUserRequest followerUserRequest = new FollowerUserRequest();
        followerUserRequest.setUsername(followerUsername);

        boolean result = userService.removeFollowerByUsername(followerUserRequest, username);

        assertTrue(result);
        assertFalse(user.getFollowers().contains(follower));
        assertFalse(follower.getFollowing().contains(user));

        verify(userRepository, times(1)).saveAndFlush(user);
        verify(userRepository, times(1)).saveAndFlush(follower);
    }

    @Test
    void testEditUserProfilePicture_UserNotFound() throws IOException {
        String username = "testUser";
        MultipartFile file = mock(MultipartFile.class);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.editUserProfilePicture(file, username));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testEditUserProfilePicture_FileUploadFailure() throws IOException {
        String username = "testUser";
        MultipartFile file = mock(MultipartFile.class);
        UserEntity user = new UserEntity();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(cloudinaryService.uploadImage(file)).thenThrow(new IOException("Cloudinary upload error"));

        assertThrows(IOException.class, () -> userService.editUserProfilePicture(file, username));
        verify(userRepository, times(1)).findByUsername(username);
        verify(cloudinaryService, times(1)).uploadImage(file);
    }

    @Test
    void testGetUserById_UserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserDetails_UserNotFound() {
        String username = "nonexistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserDetails(username));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testIsUserBlocked_UserNotFound() {
        String username = "testUser";
        String blockedUsername = "blockedUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.isUserBlocked(username, blockedUsername));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testIsUserBlocked_BlockedUserNotFound() {
        String username = "testUser";
        String blockedUsername = "blockedUser";

        UserEntity user = new UserEntity();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(blockedUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.isUserBlocked(username, blockedUsername));
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(blockedUsername);
    }

    @Test
    void testGetBlockedUsers_UserNotFound() {
        String username = "testUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getBlockedUsers(username));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testUnblockUser_UserNotFound() {
        String username = "testUser";
        String blockedUsername = "blockedUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.unblockUser(username, blockedUsername));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testUnblockUser_BlockedUserNotFound() {
        String username = "testUser";
        String blockedUsername = "blockedUser";

        UserEntity user = new UserEntity();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(blockedUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.unblockUser(username, blockedUsername));
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(blockedUsername);
    }

    @Test
    void testBlockUser_UserNotFound() {
        String username = "testUser";
        String blockedUsername = "blockedUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.blockUser(username, blockedUsername));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testBlockUser_BlockedUserNotFound() {
        String username = "testUser";
        String blockedUsername = "blockedUser";

        UserEntity user = new UserEntity();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(blockedUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.blockUser(username, blockedUsername));
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(blockedUsername);
    }

    @Test
    void testRemoveFollower_UserNotFound() {
        String username = "testUser";
        FollowerUserRequest followerUserRequest = new FollowerUserRequest();
        followerUserRequest.setUsername("followerUser");

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.removeFollowerByUsername(followerUserRequest, username));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testRemoveFollower_FollowerNotFound() {
        String username = "testUser";
        FollowerUserRequest followerUserRequest = new FollowerUserRequest();
        followerUserRequest.setUsername("followerUser");

        UserEntity user = new UserEntity();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(followerUserRequest.getUsername())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.removeFollowerByUsername(followerUserRequest, username));
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(followerUserRequest.getUsername());
    }

    @Test
    void testGetAllFriends_UserNotFound() {
        String username = "testUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getAllFriends(username));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetAllFriends_Success() {
        String username = "testUser";
        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity friend1 = new UserEntity();
        friend1.setUsername("friend1");

        user.getFriends().add(friend1);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Set<FriendsResponse> friendsResponse = userService.getAllFriends(username);

        assertNotNull(friendsResponse);
        assertEquals(1, friendsResponse.size());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetAllFollowings_UserNotFound() {
        String username = "nonExistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getAllFollowings(username);
        });
    }

    @Test
    void testGetAllFollowings_Success() {
        String username = "testUser";
        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity following1 = new UserEntity();
        following1.setUsername("following1");

        user.getFollowing().add(following1);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Set<FollowersResponse> followersResponse = userService.getAllFollowings(username);

        assertNotNull(followersResponse);
        assertEquals(1, followersResponse.size());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetSentFriendRequests_UserNotFound() {
        String username = "nonExistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getSentFriendRequests(username);
        });
    }

    @Test
    void testGetSentFriendRequests_Success() {
        String username = "testUser";
        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity friend1 = new UserEntity();
        friend1.setUsername("friend1");

        user.getSendFriendRequest().add(friend1);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Set<FriendsResponse> friendsResponse = userService.getSentFriendRequests(username);

        assertNotNull(friendsResponse);
        assertEquals(1, friendsResponse.size());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetSentFriendRequests_EmptySentRequests() {
        String username = "testUserNoRequests";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setSendFriendRequest(new HashSet<>());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Set<FriendsResponse> friendsResponse = userService.getSentFriendRequests(username);

        assertNotNull(friendsResponse);
        assertTrue(friendsResponse.isEmpty());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetReceiveFriendRequests_UserNotFound() {
        String username = "nonExistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getReceiveFriendRequests(username);
        });
    }

    @Test
    void testGetReceiveFriendRequests_Success() {
        String username = "testUser";
        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity friend1 = new UserEntity();
        friend1.setUsername("friend1");

        user.getReceiveFriendRequest().add(friend1);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Set<FriendsResponse> friendsResponse = userService.getReceiveFriendRequests(username);

        assertNotNull(friendsResponse);
        assertEquals(1, friendsResponse.size());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetReceiveFriendRequests_EmptyReceivedRequests() {
        String username = "testUserNoRequests";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setReceiveFriendRequest(new HashSet<>());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Set<FriendsResponse> friendsResponse = userService.getReceiveFriendRequests(username);

        assertNotNull(friendsResponse);
        assertTrue(friendsResponse.isEmpty());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetAllFollowers_Success() {
        String username = "testUser";
        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity follower1 = new UserEntity();
        follower1.setUsername("follower1");

        user.getFollowers().add(follower1);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Set<FollowersResponse> followersResponse = userService.getAllFollowers(username);

        assertNotNull(followersResponse);
        assertEquals(1, followersResponse.size());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetAllFollowers_EmptyFollowers() {
        String username = "testUserNoFollowers";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setFollowers(new HashSet<>());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Set<FollowersResponse> followersResponse = userService.getAllFollowers(username);

        assertNotNull(followersResponse);
        assertTrue(followersResponse.isEmpty());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetAllFollowers_UserNotFound() {
        String username = "nonExistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getAllFollowers(username);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testAreFriends_UserNotFound() {
        String username = "user1";
        String targetUsername = "user2";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.areFriends(username, targetUsername);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testAreFriends_Success() {
        String username = "user1";
        String targetUsername = "user2";

        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity targetUser = new UserEntity();
        targetUser.setUsername(targetUsername);

        Set<UserEntity> friends = new HashSet<>();
        friends.add(targetUser);
        user.setFriends(friends);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(targetUser));

        boolean areFriends = userService.areFriends(username, targetUsername);

        assertTrue(areFriends);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(targetUsername);
    }


    @Test
    void testHasSentFriendRequest_UserNotFound() {
        String username = "user1";
        String targetUsername = "user2";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.hasSentFriendRequest(username, targetUsername);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    void testHasSentFriendRequest_Success() {
        String username = "user1";
        String targetUsername = "user2";

        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity targetUser = new UserEntity();
        targetUser.setUsername(targetUsername);

        Set<UserEntity> sentRequests = new HashSet<>();
        sentRequests.add(targetUser);
        user.setSendFriendRequest(sentRequests);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(targetUser));

        boolean hasSentRequest = userService.hasSentFriendRequest(username, targetUsername);

        assertTrue(hasSentRequest);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(targetUsername);
    }


    @Test
    void testIsFollowing_UserNotFound() {
        String username = "user1";
        String targetUsername = "user2";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.isFollowing(username, targetUsername);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    void testIsFollowing_Success() {
        String username = "user1";
        String targetUsername = "user2";

        UserEntity user = new UserEntity();
        user.setUsername(username);

        UserEntity targetUser = new UserEntity();
        targetUser.setUsername(targetUsername);

        Set<UserEntity> followers = new HashSet<>();
        followers.add(user);
        targetUser.setFollowers(followers);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(targetUser));

        boolean isFollowing = userService.isFollowing(username, targetUsername);

        assertTrue(isFollowing);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(targetUsername);
    }

    @Test
    void testAreFriends_UserNotFound_Username() {
        String username = "user1";
        String targetUsername = "user2";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.areFriends(username, targetUsername);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    void testAreFriends_UserNotFound_TargetUsername() {
        String username = "user1";
        String targetUsername = "user2";

        UserEntity user = new UserEntity();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.areFriends(username, targetUsername);
        });

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(targetUsername);
    }


    @Test
    void testHasSentFriendRequest_UserNotFound_Username() {
        String username = "user1";
        String targetUsername = "user2";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.hasSentFriendRequest(username, targetUsername);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    void testHasSentFriendRequest_UserNotFound_TargetUsername() {
        String username = "user1";
        String targetUsername = "user2";

        UserEntity user = new UserEntity();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.hasSentFriendRequest(username, targetUsername);
        });

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(targetUsername);
    }


    @Test
    void testIsFollowing_UserNotFound_Username() {
        String username = "user1";
        String targetUsername = "user2";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.isFollowing(username, targetUsername);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    void testIsFollowing_UserNotFound_TargetUsername() {
        String username = "user1";
        String targetUsername = "user2";

        UserEntity user = new UserEntity();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.isFollowing(username, targetUsername);
        });

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(targetUsername);
    }

    @Test
    void testRemoveFriendByUsername_UserNotFound_Username() {
        String username = "user1";
        String friendUsername = "friend1";
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUsername(friendUsername);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.removeFriendByUsername(friendRequest, username);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    void testRemoveFriendByUsername_UserNotFound_FriendUsername() {
        String username = "user1";
        String friendUsername = "friend1";
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUsername(friendUsername);

        UserEntity user = new UserEntity();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(friendUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.removeFriendByUsername(friendRequest, username);
        });

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(friendUsername);
    }


    @Test
    void testCancelFriendRequestByUsername_UserNotFound_Username() {
        String username = "user1";
        String friendUsername = "friend1";
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUsername(friendUsername);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.cancelFriendRequestByUsername(friendRequest, username);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    void testCancelFriendRequestByUsername_UserNotFound_FriendUsername() {
        String username = "user1";
        String friendUsername = "friend1";
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUsername(friendUsername);

        UserEntity user = new UserEntity();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(friendUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.cancelFriendRequestByUsername(friendRequest, username);
        });

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(friendUsername);
    }


    @Test
    void testUnfollowUserByUsername_UserNotFound_Username() {
        String username = "user1";
        String followerUsername = "follower1";
        FollowerUserRequest followerUserRequest = new FollowerUserRequest();
        followerUserRequest.setUsername(followerUsername);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.unfollowUserByUsername(followerUserRequest, username);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    void testUnfollowUserByUsername_UserNotFound_FollowerUsername() {
        String username = "user1";
        String followerUsername = "follower1";
        FollowerUserRequest followerUserRequest = new FollowerUserRequest();
        followerUserRequest.setUsername(followerUsername);

        UserEntity user = new UserEntity();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(followerUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.unfollowUserByUsername(followerUserRequest, username);
        });

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(followerUsername);
    }


    @Test
    void testRemoveFriendByUsername_Success() {
        String username = "user1";
        String friendUsername = "friend1";
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUsername(friendUsername);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        UserEntity friend = new UserEntity();
        friend.setUsername(friendUsername);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(friendUsername)).thenReturn(Optional.of(friend));

        boolean result = userService.removeFriendByUsername(friendRequest, username);

        assertTrue(result);
        assertFalse(user.getFriends().contains(friend));
        assertFalse(friend.getFriends().contains(user));

        verify(userRepository, times(1)).saveAndFlush(user);
        verify(userRepository, times(1)).saveAndFlush(friend);
    }


    @Test
    void testCancelFriendRequestByUsername_Success() {
        String username = "user1";
        String friendUsername = "friend1";
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUsername(friendUsername);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        UserEntity friend = new UserEntity();
        friend.setUsername(friendUsername);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(friendUsername)).thenReturn(Optional.of(friend));

        boolean result = userService.cancelFriendRequestByUsername(friendRequest, username);

        assertTrue(result);
        assertFalse(user.getSendFriendRequest().contains(friend));
        assertFalse(friend.getReceiveFriendRequest().contains(user));

        verify(userRepository, times(1)).saveAndFlush(user);
        verify(userRepository, times(1)).saveAndFlush(friend);
    }


    @Test
    void testUnfollowUserByUsername_Success() {
        String username = "user1";
        String followerUsername = "follower1";
        FollowerUserRequest followerUserRequest = new FollowerUserRequest();
        followerUserRequest.setUsername(followerUsername);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        UserEntity follower = new UserEntity();
        follower.setUsername(followerUsername);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(followerUsername)).thenReturn(Optional.of(follower));

        boolean result = userService.unfollowUserByUsername(followerUserRequest, username);

        assertTrue(result);
        assertFalse(user.getFollowing().contains(follower));
        assertFalse(follower.getFollowers().contains(user));

        verify(userRepository, times(1)).saveAndFlush(user);
        verify(userRepository, times(1)).saveAndFlush(follower);
    }

    @Test
    void testRejectFriendRequest_Success() {
        String username = "user1";
        String friendUsername = "friend1";
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUsername(friendUsername);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        UserEntity friend = new UserEntity();
        friend.setUsername(friendUsername);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(friendUsername)).thenReturn(Optional.of(friend));

        user.getReceiveFriendRequest().add(friend);
        friend.getSendFriendRequest().add(user);

        // When
        boolean result = userService.rejectFriendRequest(friendRequest, username);

        assertTrue(result);
        assertFalse(user.getReceiveFriendRequest().contains(friend));
        assertFalse(friend.getSendFriendRequest().contains(user));

        verify(userRepository, times(1)).saveAndFlush(user);
        verify(userRepository, times(1)).saveAndFlush(friend);
    }

    @Test
    void testIsValidUser_Success() {
        String username = "user1";
        String password = "password123";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        boolean result = userService.isValidUser(username, password);

        assertTrue(result);

        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    void testIsValidUser_Failure_WrongPassword() {
        String username = "user1";
        String password = "wrongpassword";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword("password123");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        boolean result = userService.isValidUser(username, password);

        assertFalse(result);

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testRejectFriendRequest_UserNotFound_MainUser() {
        String username = "user1";
        String friendUsername = "friend1";
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUsername(friendUsername);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.rejectFriendRequest(friendRequest, username);
        });

        assertEquals("User with username " + username + " not found", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(0)).findByUsername(friendUsername);
    }


    @Test
    void testRejectFriendRequest_UserNotFound_Friend() {
        String username = "user1";
        String friendUsername = "friend1";
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUsername(friendUsername);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        when(userRepository.findByUsername(friendUsername)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.rejectFriendRequest(friendRequest, username);
        });

        assertEquals("User with username " + friendUsername + " not found", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(friendUsername);
    }


    @Test
    void testAcceptFriendRequest_UserNotFound_MainUser() {
        String username = "user1";
        String friendUsername = "friend1";
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUsername(friendUsername);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.acceptFriendRequest(friendRequest, username);
        });

        assertEquals("User with username " + username + " not found", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(0)).findByUsername(friendUsername);
    }

    @Test
    void testAcceptFriendRequest_UserNotFound_Friend() {
        String username = "user1";
        String friendUsername = "friend1";
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUsername(friendUsername);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        when(userRepository.findByUsername(friendUsername)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.acceptFriendRequest(friendRequest, username);
        });

        assertEquals("User with username " + friendUsername + " not found", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(friendUsername);
    }

    @Test
    void testFollowUserByUsername_UserNotFound_MainUser() {
        String username = "user1";
        String followerUsername = "follower1";
        FollowerUserRequest followerUserRequest = new FollowerUserRequest();
        followerUserRequest.setUsername(followerUsername);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.followUserByUsername(followerUserRequest, username);
        });

        assertEquals("User with username " + username + " not found", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(0)).findByUsername(followerUsername);
    }

    @Test
    void testFollowUserByUsername_UserNotFound_Follower() {
        String username = "user1";
        String followerUsername = "follower1";
        FollowerUserRequest followerUserRequest = new FollowerUserRequest();
        followerUserRequest.setUsername(followerUsername);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        when(userRepository.findByUsername(followerUsername)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.followUserByUsername(followerUserRequest, username);
        });

        assertEquals("User with username " + followerUsername + " not found", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(followerUsername);
    }

    @Test
    void testUpdateModerationPermissions_Success() {
        UUID id = UUID.randomUUID();
        UserEntity moderator = new UserEntity();
        moderator.setId(id);
        moderator.setPermissions(new HashSet<>());

        ModeratorPermissionsUpdateRequest updateRequest = new ModeratorPermissionsUpdateRequest();
        updateRequest.setPermissionsToAdd(Set.of(UserPermission.APPROVE_USERS));
        updateRequest.setPermissionsToRemove(Set.of(UserPermission.BAN_USERS));

        when(userRepository.findById(id)).thenReturn(Optional.of(moderator));
        when(userRepository.saveAndFlush(moderator)).thenReturn(moderator);

        ModeratorPermissionsResponse response = userService.updateModerationPermissions(id, updateRequest);

        assertTrue(moderator.getPermissions().contains(UserPermission.APPROVE_USERS));
        assertFalse(moderator.getPermissions().contains(UserPermission.BAN_USERS));
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).saveAndFlush(moderator);
    }

    @Test
    void testUpdateModerationPermissions_UserNotFound() {
        UUID id = UUID.randomUUID();
        ModeratorPermissionsUpdateRequest updateRequest = new ModeratorPermissionsUpdateRequest();
        updateRequest.setPermissionsToAdd(Set.of(UserPermission.APPROVE_USERS));
        updateRequest.setPermissionsToRemove(Set.of(UserPermission.BAN_USERS));

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateModerationPermissions(id, updateRequest);
        });

        assertEquals("User with ID " + id + " not found", exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void testGetAllUsersForCountries_Success() {
        UserEntity user1 = new UserEntity();
        user1.setUsername("user1");

        UserEntity user2 = new UserEntity();
        user2.setUsername("user2");

        List<UserEntity> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserEntity> result = userService.getAllUsersForCountries();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(user -> user.getUsername().equals("user1")));
        assertTrue(result.stream().anyMatch(user -> user.getUsername().equals("user2")));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetCurrentAdminPermissions_Success() {
        String username = "adminUser";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPermissions(Set.of(UserPermission.APPROVE_USERS));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        List<UserPermission> permissions = userService.getCurrentAdminPermissions(username);

        assertNotNull(permissions);
        assertEquals(1, permissions.size());
        verify(userRepository, times(1)).findByUsername(username);
    }

}