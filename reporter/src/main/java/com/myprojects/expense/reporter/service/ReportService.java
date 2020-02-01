package com.myprojects.expense.reporter.service;

import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.response.DayReportResponse;

import java.time.LocalDate;

public interface ReportService {

    default DayReportResponse getDayReport(int year, int month, int day) {
        DayReport report = getDayReport(LocalDate.of(year, month, day));
        DayReportResponse response = new DayReportResponse();
        response.setDate(report.getDate());
        response.setStats(report.getStats());
        response.setIncomes(report.getIncomes());
        response.setExpenses(report.getExpenses());
        return response;
    }

    DayReport getDayReport(LocalDate date);

    DayReport getDayReportOrCreate(LocalDate date);
}
