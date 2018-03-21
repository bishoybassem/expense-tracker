package com.myprojects.expense.reporter.model;

import java.math.BigDecimal;

public class ReportTransaction {

    private String transactionId;
    private BigDecimal amount;
    private String category;

    public ReportTransaction() {

    }

    public ReportTransaction(String transactionId, BigDecimal amount, String category) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.category = category;
    }

    public String getId() {
        return transactionId;
    }

    public void setId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
