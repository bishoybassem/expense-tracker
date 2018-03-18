package com.myprojects.expense.reporter.config;

import com.myprojects.expense.reporter.service.AggregationService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {AggregationService.class})
public class ReporterServiceConfig {

}
