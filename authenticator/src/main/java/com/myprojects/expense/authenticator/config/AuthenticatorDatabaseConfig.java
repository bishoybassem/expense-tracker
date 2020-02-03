package com.myprojects.expense.authenticator.config;

import com.myprojects.expense.authenticator.dao.AppUserDao;
import com.myprojects.expense.authenticator.model.AppUser;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = AppUserDao.class)
@EntityScan(basePackageClasses = AppUser.class)
public class AuthenticatorDatabaseConfig {

}
