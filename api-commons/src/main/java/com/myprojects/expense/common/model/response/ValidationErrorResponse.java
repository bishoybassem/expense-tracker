package com.myprojects.expense.common.model.response;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse extends ErrorResponse {

    private List<String> errors = new ArrayList<>();

    public ValidationErrorResponse(String message) {
        super(message);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        errors.add(error);
    }
}
