package com.myprojects.expense.reporter.config;

import com.myprojects.expense.reporter.service.TransactionEventHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {TransactionEventHandler.class})
public class ReporterServiceConfig {

}
