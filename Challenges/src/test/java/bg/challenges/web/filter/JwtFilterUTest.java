package bg.challenges.web.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterUTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Claims claims;

    @InjectMocks
    private JwtFilter jwtFilter;

    private final String VALID_JWT = "valid.jwt.token";
    private final String USERNAME = "testUser";
    private final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ValidToken_SetsAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_JWT);
        when(jwtService.extractUsername(VALID_JWT)).thenReturn(USERNAME);
        when(jwtService.validateToken(VALID_JWT)).thenReturn(true);
        when(jwtService.extractAllClaims(VALID_JWT)).thenReturn(claims);
        when(jwtService.extractUserId(VALID_JWT)).thenReturn(USER_ID);
        when(claims.get("role", String.class)).thenReturn("USER");
        when(claims.get("permissions", List.class)).thenReturn(List.of("banUsers", "answerFeedback"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(USERNAME, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        verify(jwtService, times(1)).extractUsername(VALID_JWT);
        verify(jwtService, times(1)).validateToken(VALID_JWT);
        verify(jwtService, times(1)).extractAllClaims(VALID_JWT);
        verify(jwtService, times(1)).extractUserId(VALID_JWT);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoToken_DoesNothing() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(jwtService, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken_DoesNothing() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_JWT);
        when(jwtService.extractUsername(VALID_JWT)).thenReturn(USERNAME);
        when(jwtService.validateToken(VALID_JWT)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain, times(1)).doFilter(request, response);
    }

}