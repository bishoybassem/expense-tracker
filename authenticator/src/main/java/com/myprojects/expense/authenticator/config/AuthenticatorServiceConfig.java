package com.myprojects.expense.authenticator.config;

import com.myprojects.expense.authenticator.service.AppUserService;
import com.myprojects.expense.common.config.JwtConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Import(JwtConfig.class)
@ComponentScan(basePackageClasses = AppUserService.class)
public class AuthenticatorServiceConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
