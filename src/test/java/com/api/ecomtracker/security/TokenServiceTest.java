package com.api.ecomtracker.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.api.ecomtracker.domain.Role;
import com.api.ecomtracker.domain.User;
import com.api.ecomtracker.exception.InvalidTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TokenService")
class TokenServiceTest {

    private final TokenService tokenService = new TokenService("test-secret", "test-issuer", 2);

    private final User user =
            new User("user@example.com", "user", "encoded", new Role(1L, Role.RoleName.USER));

    @Test
    @DisplayName("generateToken and getSubject should round-trip the user email")
    void generateAndReadTokenShouldRoundTrip() {
        String token = tokenService.generateToken(user);

        assertThat(tokenService.getSubject(token)).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("getSubject should reject malformed tokens")
    void getSubjectShouldRejectMalformedToken() {
        assertThatThrownBy(() -> tokenService.getSubject("not-a-token"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("getSubject should reject tokens signed with a different secret")
    void getSubjectShouldRejectTokenFromOtherSecret() {
        String token = new TokenService("other-secret", "test-issuer", 2).generateToken(user);

        assertThatThrownBy(() -> tokenService.getSubject(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("getSubject should reject tokens from a different issuer")
    void getSubjectShouldRejectTokenFromOtherIssuer() {
        String token = new TokenService("test-secret", "other-issuer", 2).generateToken(user);

        assertThatThrownBy(() -> tokenService.getSubject(token))
                .isInstanceOf(InvalidTokenException.class);
    }
}
