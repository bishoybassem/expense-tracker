package com.myprojects.expense.tracker.config;

import com.myprojects.expense.common.config.BaseWebSecurityConfig;
import com.myprojects.expense.common.config.JwtConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@Import(JwtConfig.class)
public class TrackerWebSecurityConfig extends BaseWebSecurityConfig {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.authorizeRequests()
                .antMatchers("/actuator/**").hasIpAddress("127.0.0.1")
                .anyRequest().authenticated();
    }
}