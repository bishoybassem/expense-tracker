package com.myprojects.expense.authenticator.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.myprojects.expense.authenticator.service.AppUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Configuration
@ComponentScan(basePackageClasses = AppUserService.class)
public class AuthenticatorServiceConfig {

    @Bean
    public Algorithm jwtAlgorithm(@Value("${jwt.algorithm.secret}") String jwtAlgorithmSecret) {
        return HMAC512(jwtAlgorithmSecret);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
