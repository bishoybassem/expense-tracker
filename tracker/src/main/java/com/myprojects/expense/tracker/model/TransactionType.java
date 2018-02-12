package com.myprojects.expense.tracker.model;

public enum TransactionType {

    INCOME(true),
    EXPENSE(false);

    private boolean booleanValue;

    TransactionType(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }
}
