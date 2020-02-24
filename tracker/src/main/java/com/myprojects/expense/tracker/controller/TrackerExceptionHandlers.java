package com.myprojects.expense.tracker.controller;

import com.myprojects.expense.common.model.response.ErrorResponse;
import com.myprojects.expense.tracker.exception.TransactionNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TrackerExceptionHandlers {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleTransactionNotFoundException(TransactionNotFoundException exception) {
        return new ErrorResponse("Transaction with id (" + exception.getTransactionId() + ") is not found!");
    }

}
