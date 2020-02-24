package com.myprojects.expense.common.config;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JwtConfig {

    @Bean
    public Algorithm jwtAlgorithm(@Value("${jwt.algorithm.secret}") String jwtAlgorithmSecret) {
        return HMAC512(jwtAlgorithmSecret);
    }

}
