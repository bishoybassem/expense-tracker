package com.myprojects.expense.reporter.config;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ReporterAppConfig {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ReporterAppConfig.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}
