package com.myprojects.expense.reporter.config;

import com.myprojects.expense.reporter.controller.ReportController;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = ReportController.class)
public class ReporterControllerConfig {

}
