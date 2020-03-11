package com.myprojects.expense.common.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myprojects.expense.common.filter.JwtVerificationFilter;
import com.myprojects.expense.common.model.response.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import javax.servlet.http.HttpServletResponse;

public class BaseWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Algorithm jwtAlgorithm;

    /**
     * Creates an {@link AuthenticationEntryPoint}, which responds with a forbidden http response in case of any
     * {@link AuthenticationException}. If the exception is caused by a {@link TokenExpiredException}, then it
     * would add more details about the token expiry date in the response message.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            String responseMsg = "Access denied!";
            if (authException.getCause() instanceof TokenExpiredException) {
                responseMsg = authException.getCause().getMessage();
            }
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().print(objectMapper.writeValueAsString(new ErrorResponse(responseMsg)));
            response.getWriter().flush();
        };
    }

    /**
     * Sets a common Spring security configuration for the microservices, mainly adding the
     * {@link JwtVerificationFilter} to the filter stack, and setting the {@link AuthenticationEntryPoint} to
     * {@link BaseWebSecurityConfig#authenticationEntryPoint()}.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtAlgorithm);
        http.cors().disable()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterAfter(jwtVerificationFilter, ExceptionTranslationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint());
    }
}
