package bg.photographyjava.web.controllers;

import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.dto.UserLoginRequest;
import bg.photographyjava.web.dto.UserLoginResponse;
import bg.photographyjava.web.dto.UserRegisterRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @MockitoBean
    private JWTService jwtService;

    @BeforeEach
    void setUp() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testUser", null, AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN"));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetRegistrationInfo() throws Exception {
        MockHttpServletRequestBuilder sendRequest = get("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countries").exists());
    }

}