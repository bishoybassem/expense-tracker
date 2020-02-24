package com.myprojects.expense.reporter.config;

import com.myprojects.expense.common.controller.GenericExceptionHandlers;
import com.myprojects.expense.common.filter.RequestLoggingFilter;
import com.myprojects.expense.reporter.controller.ReportController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@Configuration
@ComponentScan(basePackageClasses = {ReportController.class, GenericExceptionHandlers.class})
public class ReporterControllerConfig {

    @Bean
    public AbstractRequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter(false);
    }

}
