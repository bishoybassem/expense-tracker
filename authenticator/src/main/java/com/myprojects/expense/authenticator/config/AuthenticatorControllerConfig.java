package com.myprojects.expense.authenticator.config;

import com.myprojects.expense.authenticator.controller.AppUserController;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = AppUserController.class)
public class AuthenticatorControllerConfig {

}
