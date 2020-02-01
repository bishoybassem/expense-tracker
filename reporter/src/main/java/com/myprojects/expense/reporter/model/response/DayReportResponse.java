package com.myprojects.expense.reporter.model.response;

import com.myprojects.expense.reporter.model.ReportStats;
import com.myprojects.expense.reporter.model.ReportTransaction;

import java.time.LocalDate;
import java.util.List;

public class DayReportResponse {

    private LocalDate date;
    private ReportStats stats;
    private List<ReportTransaction> incomes;
    private List<ReportTransaction> expenses;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ReportStats getStats() {
        return stats;
    }

    public void setStats(ReportStats stats) {
        this.stats = stats;
    }

    public List<ReportTransaction> getIncomes() {
        return incomes;
    }

    public void setIncomes(List<ReportTransaction> incomes) {
        this.incomes = incomes;
    }

    public List<ReportTransaction> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ReportTransaction> expenses) {
        this.expenses = expenses;
    }
}
