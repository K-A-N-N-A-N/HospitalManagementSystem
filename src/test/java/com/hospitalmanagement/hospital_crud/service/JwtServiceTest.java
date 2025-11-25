package com.hospitalmanagement.hospital_crud.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hospitalmanagement.hospital_crud.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        Field secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtService, "my-test-secret");

        Field expiryField = JwtService.class.getDeclaredField("expiryMs");
        expiryField.setAccessible(true);
        expiryField.set(jwtService, 60000L);
    }

    @Test
    @DisplayName("Should generate JWT token with correct claims")
    void generateToken_shouldContainClaims() {
        String token = jwtService.generateToken("U1", "john", Role.ADMIN);

        assertNotNull(token);

        DecodedJWT decoded = jwtService.decode(token);

        assertEquals("U1", decoded.getSubject());
        assertEquals("john", decoded.getClaim("username").asString());
        assertEquals("ADMIN", decoded.getClaim("role").asString());
    }

    @Test
    @DisplayName("Should validate a valid token")
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken("U1", "john", Role.ADMIN);

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    @DisplayName("Should return false for invalid or tampered token")
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        String invalid = "invalid.token.value";

        assertFalse(jwtService.isTokenValid(invalid));
    }

    @Test
    @DisplayName("Should decode JWT token successfully")
    void decode_shouldReturnDecodedJwt() {
        String token = jwtService.generateToken("U1", "john", Role.ADMIN);

        DecodedJWT decoded = jwtService.decode(token);

        assertNotNull(decoded);
        assertEquals("U1", decoded.getSubject());
    }

    @Test
    @DisplayName("expiryDate() should return future timestamp")
    void expiryDate_shouldReturnFutureTime() {
        Date expiry = jwtService.expiryDate();

        assertTrue(expiry.getTime() > System.currentTimeMillis());
    }
}
