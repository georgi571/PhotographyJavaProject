package bg.photographyjava.web.controllers;

import bg.photographyjava.user.property.enums.UserPermission;
import bg.photographyjava.user.property.enums.UserRole;
import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.dto.*;
import bg.photographyjava.web.filter.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

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
    void testGetAllUsers() throws Exception {
        ChangeRoleUserResponse response = new ChangeRoleUserResponse();
        List<ChangeRoleUserResponse> responseList = List.of(response);

        when(userService.getAllUsers()).thenReturn(responseList);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/admin/change-roles")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testUpdateUserRole() throws Exception {
        UUID userId = UUID.randomUUID();
        RoleRequest request = new RoleRequest();
        request.setRole(UserRole.ADMIN.name());
        ChangeRoleUserResponse response = new ChangeRoleUserResponse();
        response.setRole(UserRole.ADMIN.name());

        when(userService.updateUserRole(any(), any())).thenReturn(response);

        MockHttpServletRequestBuilder sendRequest = put("/api/v1/admin/change-roles/{id}", userId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").isNotEmpty());

        verify(userService, times(1)).updateUserRole(any(), any());
    }

    @Test
    void testGetUsersForBan() throws Exception {
        BanUserResponse response = new BanUserResponse();
        List<BanUserResponse> responseList = List.of(response);

        when(userService.getAllUsersForBan()).thenReturn(responseList);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/admin/ban-users")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(userService, times(1)).getAllUsersForBan();
    }

    @Test
    void testBanOrUnbanUser() throws Exception {
        UUID userId = UUID.randomUUID();
        BanUserReasonRequest request = new BanUserReasonRequest();
        request.setAction("Ban");
        request.setReason("Test Ban");

        BanUserResponse response = new BanUserResponse();
        response.setBanned(true);
        response.setReasonForBan("Test Ban");

        when(userService.banUserAction(any(), any())).thenReturn(response);

        MockHttpServletRequestBuilder sendRequest = put("/api/v1/admin/ban-users/{id}", userId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reasonForBan").isNotEmpty());

        verify(userService, times(1)).banUserAction(any(), any());
    }

    @Test
    void testGetUsersForApprove() throws Exception {
        ApproveUsersResponse response = new ApproveUsersResponse();
        List<ApproveUsersResponse> responseList = List.of(response);

        when(userService.getAllUsersForApprove()).thenReturn(responseList);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/admin/approve-users")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(userService, times(1)).getAllUsersForApprove();
    }

    @Test
    void testApproveUser() throws Exception {
        UUID userId = UUID.randomUUID();
        ApproveUserReasonRequest request = new ApproveUserReasonRequest();
        request.setAction("approve");
        ApproveUsersResponse response = new ApproveUsersResponse();
        response.setId(userId);

        when(userService.approveUserAction(any(), any())).thenReturn(response);

        MockHttpServletRequestBuilder sendRequest = put("/api/v1/admin/approve-users/{id}", userId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());

        verify(userService, times(1)).approveUserAction(any(), any());
    }

    @Test
    void testGetAdminsWithPermissions() throws Exception {
        AdminPermissionsResponse response = new AdminPermissionsResponse();
        List<AdminPermissionsResponse> responseList = List.of(response);

        when(userService.getAllAdminsWithPermissions()).thenReturn(responseList);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/admin/admin-permissions")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(userService, times(1)).getAllAdminsWithPermissions();
    }

    @Test
    void testUpdateAdminPermissions() throws Exception {
        UUID adminId = UUID.randomUUID();
        AdminPermissionsUpdateRequest updateRequest = new AdminPermissionsUpdateRequest();
        updateRequest.setPermissionsToAdd(Set.of(UserPermission.APPROVE_USERS));
        AdminPermissionsResponse response = new AdminPermissionsResponse();
        response.setPermissions(Set.of(UserPermission.APPROVE_USERS));

        when(userService.updateAdminPermissions(any(), any())).thenReturn(response);

        MockHttpServletRequestBuilder sendRequest = put("/api/v1/admin/admin-permissions/{id}", adminId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(updateRequest));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions[0]").isNotEmpty());

        verify(userService, times(1)).updateAdminPermissions(any(), any());
    }

    @Test
    void testGetModeratorsWithPermissions() throws Exception {
        ModeratorPermissionsResponse response = new ModeratorPermissionsResponse();
        List<ModeratorPermissionsResponse> responseList = List.of(response);

        when(userService.getAllModeratorsWithPermissions()).thenReturn(responseList);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/admin/moderator-permissions")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(userService, times(1)).getAllModeratorsWithPermissions();
    }

    @Test
    void testUpdateModeratorPermissions() throws Exception {
        UUID moderatorId = UUID.randomUUID();
        ModeratorPermissionsUpdateRequest updateRequest = new ModeratorPermissionsUpdateRequest();
        updateRequest.setPermissionsToAdd(Set.of(UserPermission.DELETE_MESSAGE));
        ModeratorPermissionsResponse response = new ModeratorPermissionsResponse();
        response.setPermissions(Set.of(UserPermission.DELETE_MESSAGE));

        when(userService.updateModerationPermissions(any(), any())).thenReturn(response);

        MockHttpServletRequestBuilder sendRequest = put("/api/v1/admin/moderator-permissions/{id}", moderatorId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(updateRequest));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions[0]").isNotEmpty());

        verify(userService, times(1)).updateModerationPermissions(any(), any());
    }

    @Test
    void testGetPermissions() throws Exception {
        UserPermission userPermission = UserPermission.CHANGE_USER_ROLES;
        List<UserPermission> responseList = List.of(userPermission);

        when(userService.getCurrentAdminPermissions(anyString())).thenReturn(responseList);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/admin/permissions")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").isNotEmpty());

        verify(userService, times(1)).getCurrentAdminPermissions(anyString());
    }

}