package com.myprojects.expense.tracker.model.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CreateTransactionRequest extends UpdateTransactionRequest {

    @NotNull
    @Pattern(regexp = "^(INCOME|EXPENSE)$", message = "has to be either 'INCOME' or 'EXPENSE'")
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
