package com.myprojects.expense.reporter.service;

import com.myprojects.expense.reporter.model.DayReport;

public interface ReportService {

    DayReport getDayReport(int year, int month, int day);

}
