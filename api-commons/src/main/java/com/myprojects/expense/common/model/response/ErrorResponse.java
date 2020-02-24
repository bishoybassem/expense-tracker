package com.myprojects.expense.common.model.response;

public class ErrorResponse {

    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String messageFormat, Object... args) {
        this.message = String.format(messageFormat, args);
    }

    public String getMessage() {
        return message;
    }
}
