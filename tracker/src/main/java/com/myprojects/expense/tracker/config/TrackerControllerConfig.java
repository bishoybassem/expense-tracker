package com.myprojects.expense.tracker.config;

import com.myprojects.expense.common.controller.GenericExceptionHandlers;
import com.myprojects.expense.common.filter.RequestLoggingFilter;
import com.myprojects.expense.tracker.controller.TransactionController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@Configuration
@ComponentScan(basePackageClasses = {TransactionController.class, GenericExceptionHandlers.class})
public class TrackerControllerConfig {

    @Bean
    public AbstractRequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter(true);
    }

}
