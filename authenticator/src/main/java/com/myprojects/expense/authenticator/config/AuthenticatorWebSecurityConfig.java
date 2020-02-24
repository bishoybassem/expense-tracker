package com.myprojects.expense.authenticator.config;

import com.myprojects.expense.authenticator.controller.AccessTokenController;
import com.myprojects.expense.authenticator.controller.AppUserController;
import com.myprojects.expense.common.config.BaseWebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthenticatorWebSecurityConfig extends BaseWebSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, AppUserController.PATH).permitAll()
                .antMatchers(AccessTokenController.PATH).permitAll()
                .antMatchers("/actuator/**").hasIpAddress("127.0.0.1")
                .anyRequest().authenticated();
    }

}
