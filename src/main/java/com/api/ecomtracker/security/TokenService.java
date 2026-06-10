package com.api.ecomtracker.security;

import com.api.ecomtracker.domain.User;
import com.api.ecomtracker.exception.InvalidTokenException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final String secret;

    private final String issuer;

    private final long expirationHours;

    public TokenService(
            @Value("${api.security.token.secret}") String secret,
            @Value("${api.security.token.issuer}") String issuer,
            @Value("${api.security.token.expiration-hours}") long expirationHours) {
        this.secret = secret;
        this.issuer = issuer;
        this.expirationHours = expirationHours;
    }

    public String generateToken(User user) {
        try {
            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(user.getEmail())
                    .withClaim("role", user.getRole().getName().name())
                    .withExpiresAt(expirationDate())
                    .sign(algorithm());
        } catch (JWTCreationException exception) {
            throw new IllegalStateException("Could not generate token", exception);
        }
    }

    public String getSubject(String token) {
        try {
            return JWT.require(algorithm()).withIssuer(issuer).build().verify(token).getSubject();
        } catch (JWTVerificationException exception) {
            throw new InvalidTokenException("Invalid or expired token", exception);
        }
    }

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    private Instant expirationDate() {
        return Instant.now().plus(expirationHours, ChronoUnit.HOURS);
    }
}
