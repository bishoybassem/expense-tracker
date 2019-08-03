package com.myprojects.expense.reporter.service;

import com.myprojects.expense.reporter.model.DayReport;

import java.time.LocalDate;

public interface ReportService {

    default DayReport getDayReport(int year, int month, int day) {
        return getDayReport(LocalDate.of(year, month, day));
    }

    DayReport getDayReport(LocalDate date);

    DayReport getDayReportOrCreate(LocalDate date);
}
