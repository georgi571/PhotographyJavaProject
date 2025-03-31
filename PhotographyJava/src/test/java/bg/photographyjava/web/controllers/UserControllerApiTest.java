package bg.photographyjava.web.controllers;

import bg.photographyjava.user.property.enums.CountryEnum;
import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.dto.*;
import bg.photographyjava.web.filter.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JWTService jwtService;

    @BeforeEach
    void setUp() {
        when(jwtService.validateToken(anyString())).thenReturn(true);
        when(jwtService.extractUsername(anyString())).thenReturn("testUser");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testUser", null, AuthorityUtils.createAuthorityList(
                "ROLE_USER", "PERMISSION_changeUserRoles", "ROLE_ADMIN", "PERMISSION_deleteMessage"
        ));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetUserById() throws Exception {
        UUID userId = UUID.randomUUID();
        UserInformationForPictureResponse response = new UserInformationForPictureResponse();
        response.setUsername("testUser");
        response.setId(userId);
        when(userService.getUserById(userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andExpect(jsonPath("$.id").isNotEmpty());

        verify(userService, times(1)).getUserById(any());
    }

    @Test
    void testGetUserProfileInfo() throws Exception {
        String username = "testUser";
        UserProfileResponse response = new UserProfileResponse();
        response.setUsername(username);
        response.setCountry(CountryEnum.BULGARIA.getCountryName());
        when(userService.getProfileDetails(username)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/profile/username/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andExpect(jsonPath("$.country").isNotEmpty());

        verify(userService, times(1)).getProfileDetails(any());
    }

    @Test
    void testGetUserEditProfileDetails() throws Exception {
        String username = "testUser";
        UserEditProfileResponse response = new UserEditProfileResponse();
        response.setRealName("Test User");
        response.setCity("Test City");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(userService.getProfileEditDetails(username)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/profile/edit")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.realName").isNotEmpty())
                .andExpect(jsonPath("$.city").isNotEmpty());

        verify(userService, times(1)).getProfileEditDetails(any());
    }

    @Test
    void testUpdateUserDetails() throws Exception {
        UserEditProfileRequest request = new UserEditProfileRequest();
        request.setCity("New City");
        request.setRealName("New RealName");
        request.setBirthDate(LocalDate.now().minusYears(1));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        doNothing().when(userService).editUserDetails(any(), any());

        MockHttpServletRequestBuilder sendRequest = put("/api/v1/users/profile/edit")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk());

        verify(userService, times(1)).editUserDetails(any(), any());
    }

    @Test
    void testChangeUserUsername() throws Exception {
        UserChangeUsernameRequest request = new UserChangeUsernameRequest();
        request.setOldUsername("oldUsername");
        request.setNewUsername("newUsername");
        request.setPassword("ValidPassword");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        doNothing().when(userService).editUserUsernameDetails(any(), any());

        MockHttpServletRequestBuilder sendRequest = put("/api/v1/users/profile/edit/username")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk());

        verify(userService, times(1)).editUserUsernameDetails(any(), any());
    }

    @Test
    void testChangeUserEmail() throws Exception {
        UserChangeEmailRequest request = new UserChangeEmailRequest();
        request.setOldEmail("oldEmail@abv.bg");
        request.setNewEmail("newEmail@abv.bg");
        request.setPassword("ValidPassword");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        doNothing().when(userService).editUserEmailDetails(any(), any());

        MockHttpServletRequestBuilder sendRequest = put("/api/v1/users/profile/edit/email")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk());

        verify(userService, times(1)).editUserEmailDetails(any(), any());
    }

    @Test
    void testUpdatePassword() throws Exception {
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("NewPassword");
        request.setConfirmPassword("NewPassword");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        doNothing().when(userService).updatePassword(any(), any());

        MockHttpServletRequestBuilder sendRequest = put("/api/v1/users/profile/edit/password")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk());

        verify(userService, times(1)).updatePassword(any(), any());
    }

    @Test
    void testAddFriend() throws Exception {
        FriendRequest request = new FriendRequest();
        request.setUsername("testUser2");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.addFriendByUsername(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/users/add-friend")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).addFriendByUsername(any(), any());
    }

    @Test
    void testFollowUser() throws Exception {
        FollowerUserRequest request = new FollowerUserRequest();
        request.setUsername("testUser2");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.followUserByUsername(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/users/follow-user")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).followUserByUsername(any(), any());
    }

    @Test
    void testUnfollowUser() throws Exception {
        FollowerUserRequest request = new FollowerUserRequest();
        request.setUsername("testUser2");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.unfollowUserByUsername(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/users/unfollow-user")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).unfollowUserByUsername(any(), any());
    }

    @Test
    void testAcceptFriendRequest() throws Exception {
        FriendRequest request = new FriendRequest();
        request.setUsername("testUser2");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.acceptFriendRequest(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/users/accept-friend-request")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).acceptFriendRequest(any(), any());
    }

    @Test
    void testRejectFriendRequest() throws Exception {
        FriendRequest request = new FriendRequest();
        request.setUsername("testUser2");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.rejectFriendRequest(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/users/reject-friend-request")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).rejectFriendRequest(any(), any());
    }

    @Test
    void testRemoveFriend() throws Exception {
        FriendRequest request = new FriendRequest();
        request.setUsername("testUser2");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.removeFriendByUsername(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/users/remove-friend")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).removeFriendByUsername(any(), any());
    }

    @Test
    void testCancelFriendRequest() throws Exception {
        FriendRequest request = new FriendRequest();
        request.setUsername("testUser2");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.cancelFriendRequestByUsername(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/users/cancel-friend-request")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).cancelFriendRequestByUsername(any(), any());
    }

    @Test
    void testRemoveFollower() throws Exception {
        FollowerUserRequest request = new FollowerUserRequest();
        request.setUsername("testUser2");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.removeFollowerByUsername(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/users/remove-follower")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).removeFollowerByUsername(any(), any());
    }

    @Test
    void testCheckIfFriends() throws Exception {
        String targetUsername = "testUser2";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.areFriends(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/are-friends")
                .header("Authorization", "Bearer mock-valid-token")
                .param("targetUsername", targetUsername);

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).areFriends(any(), any());
    }

    @Test
    void testCheckIfFriendRequestSent() throws Exception {
        String targetUsername = "testUser2";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.hasSentFriendRequest(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/has-sent-friend-request")
                .header("Authorization", "Bearer mock-valid-token")
                .param("targetUsername", targetUsername);

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).hasSentFriendRequest(any(), any());
    }

    @Test
    void testCheckIfFollowing() throws Exception {
        String targetUsername = "testUser2";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.isFollowing(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/is-following")
                .header("Authorization", "Bearer mock-valid-token")
                .param("targetUsername", targetUsername);

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).isFollowing(any(), any());
    }

    @Test
    void testGetSentFriendRequests() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        Set<FriendsResponse> mockResponse = new HashSet<>();
        mockResponse.add(new FriendsResponse());
        when(userService.getSentFriendRequests(any())).thenReturn(mockResponse);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/sent-requests")
                .header("Authorization", "Bearer mock-valid-token");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(userService, times(1)).getSentFriendRequests(any());
    }

    @Test
    void testGetReceiveFriendRequests() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        Set<FriendsResponse> mockResponse = new HashSet<>();
        mockResponse.add(new FriendsResponse());
        when(userService.getReceiveFriendRequests(any())).thenReturn(mockResponse);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/received-requests")
                .header("Authorization", "Bearer mock-valid-token");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(userService, times(1)).getReceiveFriendRequests(any());
    }

    @Test
    void testGetAllFollowers() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        Set<FollowersResponse> mockResponse = new HashSet<>();
        mockResponse.add(new FollowersResponse());
        when(userService.getAllFollowers(any())).thenReturn(mockResponse);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/followers")
                .header("Authorization", "Bearer mock-valid-token");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(userService, times(1)).getAllFollowers(any());
    }

    @Test
    void testGetAllFollowings() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        Set<FollowersResponse> mockResponse = new HashSet<>();
        mockResponse.add(new FollowersResponse());
        when(userService.getAllFollowings(any())).thenReturn(mockResponse);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/following")
                .header("Authorization", "Bearer mock-valid-token");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(userService, times(1)).getAllFollowings(any());
    }

    @Test
    void testGetAllFriends() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        Set<FriendsResponse> mockResponse = new HashSet<>();
        mockResponse.add(new FriendsResponse());
        when(userService.getAllFriends(any())).thenReturn(mockResponse);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/friends")
                .header("Authorization", "Bearer mock-valid-token");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(userService, times(1)).getAllFriends(any());
    }

    @Test
    void testBlockUser() throws Exception {
        String blockedUsername = "testUser2";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.blockUser(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/users/block")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(blockedUsername));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).blockUser(any(), any());
    }

    @Test
    void testUnblockUser() throws Exception {
        String blockedUsername = "testUser2";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.unblockUser(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/users/unblock")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(blockedUsername));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).unblockUser(any(), any());
    }

    @Test
    void testGetBlockedUsers() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        Set<BlockedUserResponse> mockResponse = new HashSet<>();
        mockResponse.add(new BlockedUserResponse());
        when(userService.getBlockedUsers(any())).thenReturn(mockResponse);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/blocked-users")
                .header("Authorization", "Bearer mock-valid-token");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(userService, times(1)).getBlockedUsers(any());
    }

    @Test
    void testIsUserBlocked() throws Exception {
        String username = "testUser2";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.isUserBlocked(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/is-blocked/{username}", username)
                .header("Authorization", "Bearer mock-valid-token");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).isUserBlocked(any(), any());
    }

    @Test
    void testGetUserInfo() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        ContactUserResponse mockResponse = new ContactUserResponse();
        when(userService.getUserDetails(any())).thenReturn(mockResponse);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/user-info")
                .header("Authorization", "Bearer mock-valid-token");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserDetails(any());
    }

    @Test
    void testGetFriends() throws Exception {
        String username = "testUser";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        Set<FriendsResponse> mockResponse = new HashSet<>();
        mockResponse.add(new FriendsResponse());
        when(userService.getAllFriends(any())).thenReturn(mockResponse);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/curr/friends")
                .header("Authorization", "Bearer mock-valid-token")
                .param("username", username);

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(userService, times(1)).getAllFriends(any());
    }

    @Test
    void testGetFollowers() throws Exception {
        String username = "testUser";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        Set<FollowersResponse> mockResponse = new HashSet<>();
        mockResponse.add(new FollowersResponse());
        when(userService.getAllFollowers(any())).thenReturn(mockResponse);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/users/curr/followers")
                .header("Authorization", "Bearer mock-valid-token")
                .param("username", username);

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(userService, times(1)).getAllFollowers(any());
    }

    @Test
    void testUpdateProfilePicture() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("profile-pic.jpg");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");

        when(userService.editUserProfilePicture(any(), any())).thenReturn(true);

        MockHttpServletRequestBuilder sendRequest = multipart("/api/v1/users/profile/edit/picture")
                .file("file", "dummy file content".getBytes()) // Mock file content
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA);

        mockMvc.perform(sendRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).editUserProfilePicture(any(), any());
    }

}