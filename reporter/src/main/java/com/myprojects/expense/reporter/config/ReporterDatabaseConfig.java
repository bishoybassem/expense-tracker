package com.myprojects.expense.reporter.config;

import com.myprojects.expense.reporter.dao.DayReportDao;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackageClasses = DayReportDao.class)
public class ReporterDatabaseConfig {

}
