package com.myprojects.expense.authenticator.test;

import com.myprojects.expense.authenticator.config.AuthenticatorWebSecurityConfig;
import com.myprojects.expense.authenticator.controller.AuthenticatorExceptionHandlers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Import(AuthenticatorWebSecurityConfig.class)
public class ControllerTestsBaseConfig {

    @Bean
    public AuthenticatorExceptionHandlers exceptionHandlers() {
        return new AuthenticatorExceptionHandlers();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Mockito.mock(PasswordEncoder.class);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return Mockito.mock(UserDetailsService.class);
    }

}
