package com.myprojects.expense.reporter.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "dayReports")
public class DayReport {

    @Id
    private String id;
    private ReportDate date;
    private ReportStats stats;
    private List<ReportTransaction> incomes;
    private List<ReportTransaction> expenses;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReportDate getDate() {
        return date;
    }

    public void setDate(ReportDate date) {
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
