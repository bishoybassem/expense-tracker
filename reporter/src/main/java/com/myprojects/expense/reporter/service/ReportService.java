package com.myprojects.expense.reporter.service;

import com.myprojects.expense.reporter.exception.ReportNotFoundException;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.response.DayReportResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.UUID;

public interface ReportService {

    /**
     * Queries and returns the day report owned by the authenticated user and with the given date, If found, it
     * populates and returns a {@link DayReportResponse} with the report's details.
     *
     * If none found, it throws a {@link ReportNotFoundException}.
     */
    default DayReportResponse getDayReport(int year, int month, int day) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID ownerId = (UUID) authentication.getPrincipal();
        
        DayReport report = getDayReport(LocalDate.of(year, month, day), ownerId);
        DayReportResponse response = new DayReportResponse();
        response.setDate(report.getDate());
        response.setStats(report.getStats());
        response.setIncomes(report.getIncomes());
        response.setExpenses(report.getExpenses());
        return response;
    }

    DayReport getDayReport(LocalDate date, UUID ownerId);

    DayReport getDayReportOrCreate(LocalDate date, UUID ownerId);
}
