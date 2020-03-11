package com.myprojects.expense.authenticator.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.myprojects.expense.authenticator.model.request.LoginRequest;
import com.myprojects.expense.authenticator.model.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JWTAccessTokenService implements AccessTokenService {

    @Autowired
    private Algorithm jwtAlgorithm;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${jwt.validity-duration-minutes}")
    private int jwtValidityDurationMinutes;

    /**
     * Attempts to authenticate the user based on the given {@link LoginRequest}. It delegates the authentication
     * to Spring's {@link AuthenticationManager}, and then issues a JWT if the authentication was successful.
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authRequest);

        LoginResponse response = new LoginResponse();
        response.setToken(createJwtToken((UserDetails) authentication.getPrincipal()));
        return response;
    }

    private String createJwtToken(UserDetails userDetails) {
        Instant now = Instant.now();
        String[] authorities = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toArray(size -> new String[size]);
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now
                        .plus(Duration.ofMinutes(jwtValidityDurationMinutes))))
                .withArrayClaim("roles", authorities)
                .sign(jwtAlgorithm);
    }

}
