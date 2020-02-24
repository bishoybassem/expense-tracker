package com.myprojects.expense.authenticator.config;

import com.myprojects.expense.authenticator.controller.AppUserController;
import com.myprojects.expense.common.controller.GenericExceptionHandlers;
import com.myprojects.expense.common.filter.RequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@Configuration
@ComponentScan(basePackageClasses = {AppUserController.class, GenericExceptionHandlers.class})
public class AuthenticatorControllerConfig {

    @Bean
    public AbstractRequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter(false);
    }

}
