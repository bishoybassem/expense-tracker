package com.myprojects.expense.reporter.config;

import com.myprojects.expense.reporter.filter.RequestLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@SpringBootApplication
public class ReporterAppConfig {

    @Bean
    public AbstractRequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter();
    }

    public static void main(String[] args) {
        SpringApplication.run(ReporterAppConfig.class, args);
    }

}
