package bg.challenges.web.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceUTest {

    @InjectMocks
    private JWTService jwtService;

    private String validToken;
    private UUID userId;
    private String username = "testUser";

    @BeforeEach
    void setUp() throws Exception {
        userId = UUID.randomUUID();
        SecretKey key = getPrivateKey();

        validToken = Jwts.builder()
                .subject(username)
                .claim("userId", userId.toString())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(key)
                .compact();
    }

    @Test
    void testExtractUsername() {
        String extractedUsername = jwtService.extractUsername(validToken);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testExtractUserId() {
        UUID extractedUserId = jwtService.extractUserId(validToken);
        assertEquals(userId, extractedUserId);
    }

    @Test
    void testExtractAllClaims() {
        Claims claims = jwtService.extractAllClaims(validToken);
        assertEquals(username, claims.getSubject());
        assertEquals(userId.toString(), claims.get("userId"));
    }

    @Test
    void testValidateToken_ValidToken() {
        assertTrue(jwtService.validateToken(validToken));
    }

    @Test
    void testValidateToken_ExpiredToken() throws Exception {
        SecretKey key = getPrivateKey();
        String expiredToken = Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60))
                .signWith(key)
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtService.validateToken(expiredToken));
    }

    private SecretKey getPrivateKey() throws Exception {
        Method method = JWTService.class.getDeclaredMethod("getKey");
        method.setAccessible(true);
        return (SecretKey) method.invoke(jwtService);
    }
}