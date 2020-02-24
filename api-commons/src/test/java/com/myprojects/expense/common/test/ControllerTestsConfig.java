package com.myprojects.expense.common.test;

import com.myprojects.expense.common.config.BaseWebSecurityConfig;
import com.myprojects.expense.common.config.JwtConfig;
import com.myprojects.expense.common.controller.GenericExceptionHandlers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Import(JwtConfig.class)
public class ControllerTestsConfig extends BaseWebSecurityConfig {

    @Bean
    public EchoController echoController() {
        return new EchoController();
    }

    @Bean
    public GenericExceptionHandlers genericExceptionHandlers() {
        return new GenericExceptionHandlers();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.authorizeRequests()
                .anyRequest().authenticated();
    }
}
