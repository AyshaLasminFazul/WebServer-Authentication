
package com.finflow.finflow_api.auth;

import com.finflow.finflow_api.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {

        // Test-only secret: 32 bytes encoded as Base64
        String secret = Base64.getEncoder()
                .encodeToString(new byte[32]);

        jwtService = new JwtService(secret);

        signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
    }

    @Test
    void generateToken_ShouldGenerateJwtSuccessfully() {

        User user = new User();
        user.setId(1L);
        user.setRole("CUSTOMER");

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {

        User user = new User();
        user.setId(1L);
        user.setRole("CUSTOMER");

        String token = jwtService.generateToken(user);

        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void claimExtraction_ShouldReturnCorrectUserDetails() {

        User user = new User();
        user.setId(1L);
        user.setRole("CUSTOMER");

        String token = jwtService.generateToken(user);

        Claims claims = jwtService.claimExtraction(token);

        assertEquals(1L, claims.get("userId", Long.class));
        assertEquals("CUSTOMER", claims.get("role", String.class));
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsInvalid() {

        String invalidToken = "invalid.token.value";

        assertFalse(jwtService.validateToken(invalidToken));
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsExpired() {

        Date now = new Date();

        String expiredToken = Jwts.builder()
                .claim("userId", 1L)
                .claim("role", "CUSTOMER")
                .issuedAt(new Date(now.getTime() - 60000))
                .expiration(new Date(now.getTime() - 1000))
                .signWith(signingKey)
                .compact();

        assertFalse(jwtService.validateToken(expiredToken));
    }
}
