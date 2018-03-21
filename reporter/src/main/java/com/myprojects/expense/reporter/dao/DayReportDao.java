package com.myprojects.expense.reporter.dao;

import com.myprojects.expense.reporter.model.DayReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DayReportDao extends MongoRepository<DayReport, String> {

}
