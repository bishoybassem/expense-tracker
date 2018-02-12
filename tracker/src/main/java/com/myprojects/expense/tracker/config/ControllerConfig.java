package com.myprojects.expense.tracker.config;

import com.myprojects.expense.tracker.controller.TransactionController;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = TransactionController.class)
public class ControllerConfig {

}
