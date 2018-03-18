package com.myprojects.expense.tracker.config;

import com.myprojects.expense.tracker.dao.TransactionDao;
import com.myprojects.expense.tracker.model.Transaction;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = TransactionDao.class)
@EntityScan(basePackageClasses = Transaction.class)
public class TrackerDatabaseConfig {

}
