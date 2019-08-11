package com.myprojects.expense.tracker.config;

import com.myprojects.expense.tracker.filter.RequestLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@SpringBootApplication
public class TrackerAppConfig {

    @Bean
    public AbstractRequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter();
    }

    public static void main(String[] args) {
        SpringApplication.run(TrackerAppConfig.class, args);
    }

}
