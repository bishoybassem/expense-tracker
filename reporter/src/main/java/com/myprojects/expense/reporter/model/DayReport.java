package com.myprojects.expense.reporter.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "dayReports")
public class DayReport {

    @Id
    private String id;

    @Indexed(unique = true)
    private LocalDate date;

    private ReportStats stats;
    private List<ReportTransaction> incomes;
    private List<ReportTransaction> expenses;

    @Version
    private Long version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public static DayReport emptyReport(LocalDate date) {
        ReportStats stats = new ReportStats();
        stats.setTotal(BigDecimal.ZERO);
        stats.setTotalExpenses(BigDecimal.ZERO);
        stats.setTotalIncomes(BigDecimal.ZERO);

        DayReport emptyReport = new DayReport();
        emptyReport.setDate(date);
        emptyReport.setIncomes(new ArrayList<>());
        emptyReport.setExpenses(new ArrayList<>());
        emptyReport.setStats(stats);
        return emptyReport;
    }
}
