package com.myprojects.expense.common.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.myprojects.expense.common.exception.JwtVerificationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Extracts the JWT token from the "X-Access-Token" header, and verifies it.
 */
public class JwtVerificationFilter extends OncePerRequestFilter {

    private JWTVerifier jwtVerifier;

    public JwtVerificationFilter(Algorithm jwtAlgorithm) {
        jwtVerifier = JWT.require(jwtAlgorithm)
                .build();
    }

    /**
     * If the "X-Access-Token" header is preset, it tries to verify the JWT. If successful, it updates the
     * {@link org.springframework.security.core.context.SecurityContext} with the authenticated principal (user id).
     *
     * If the JWT verification fails, it throws a {@link JwtVerificationException} with the actual cause.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String accessToken = request.getHeader("X-Access-Token");
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        DecodedJWT decodedToken;
        try {
            decodedToken = jwtVerifier.verify(accessToken);
        } catch (Exception ex) {
            throw new JwtVerificationException(ex);
        }

        populateSecurityContext(decodedToken);
        filterChain.doFilter(request, response);
    }

    private void populateSecurityContext(DecodedJWT decodedToken) {
        Set<GrantedAuthority> authorities = decodedToken.getClaim("roles")
                .asList(String.class)
                .stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toSet());

        UUID userId = UUID.fromString(decodedToken.getSubject());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null, authorities));
    }

}
