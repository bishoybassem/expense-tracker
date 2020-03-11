package com.myprojects.expense.reporter.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "dayReports")
@CompoundIndex(def = "{'ownerId': 1, 'date': -1}", name = "owner_date", unique = true)
public class DayReport {

    @Id
    private String id;

    private UUID ownerId;
    private LocalDate date;
    private ReportStats stats;
    private List<ReportTransaction> incomes;
    private List<ReportTransaction> expenses;

    @Version
    private Long version;

    public String getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
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

    public static DayReport emptyReport(LocalDate date, UUID ownerId) {
        ReportStats stats = new ReportStats();
        stats.setTotal(new BigDecimal("0.0"));
        stats.setTotalExpenses(new BigDecimal("0.0"));
        stats.setTotalIncomes(new BigDecimal("0.0"));

        DayReport emptyReport = new DayReport();
        emptyReport.setOwnerId(ownerId);
        emptyReport.setDate(date);
        emptyReport.setIncomes(new ArrayList<>());
        emptyReport.setExpenses(new ArrayList<>());
        emptyReport.setStats(stats);
        return emptyReport;
    }
}
