package com.hospitalmanagement.hospital_crud.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWTVerifier;
import com.hospitalmanagement.hospital_crud.entity.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expiryMs;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    public String generateToken(String userId, String username, Role role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiryMs);

        return JWT.create()
                .withSubject(userId)
                .withClaim("username", username)
                .withClaim("role", role.name())
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm());
    }

    public boolean isTokenValid(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm()).build();
            verifier.verify(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public DecodedJWT decode(String token) {
        JWTVerifier verifier = JWT.require(algorithm()).build();
        return verifier.verify(token);
    }

    public Date expiryDate() {
        return new Date(System.currentTimeMillis() + expiryMs);
    }
}
