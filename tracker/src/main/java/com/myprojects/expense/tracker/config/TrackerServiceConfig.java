package com.myprojects.expense.tracker.config;

import com.myprojects.expense.tracker.service.TransactionService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {TransactionService.class})
public class TrackerServiceConfig {

}
