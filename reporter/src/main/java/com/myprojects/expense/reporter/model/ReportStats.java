package com.myprojects.expense.reporter.model;

import java.math.BigDecimal;

public class ReportStats {

    private BigDecimal total;
    private BigDecimal totalIncomes;
    private BigDecimal totalExpenses;

    public ReportStats() {

    }

    public ReportStats(BigDecimal total, BigDecimal totalIncomes, BigDecimal totalExpenses) {
        this.total = total;
        this.totalIncomes = totalIncomes;
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotalIncomes() {
        return totalIncomes;
    }

    public void setTotalIncomes(BigDecimal totalIncomes) {
        this.totalIncomes = totalIncomes;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
}
