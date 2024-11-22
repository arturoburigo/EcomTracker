    package com.api.EcomTracker.controller;

    import com.api.EcomTracker.domain.users.Users;
    import com.api.EcomTracker.domain.users.dto.AuthDTO;
    import com.api.EcomTracker.infra.security.TokenJWTData;
    import com.api.EcomTracker.infra.security.TokenService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import javax.validation.Valid;

    @RestController
    @RequestMapping("/auth")
    public class AuthenticationController {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private TokenService tokenService;

        @PostMapping
        public ResponseEntity<TokenJWTData> login(@RequestBody @Valid AuthDTO data) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword());
            Authentication auth = authenticationManager.authenticate(authToken);
            String tokenJWT = tokenService.generateToken((Users) auth.getPrincipal());
            return ResponseEntity.ok(new TokenJWTData(tokenJWT));
        }
    }