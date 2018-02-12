package com.myprojects.expense.tracker.model.request;

import com.myprojects.expense.tracker.model.TransactionType;

public class CreateTransactionRequest extends UpdateTransactionRequest {

    private TransactionType type;

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

}
