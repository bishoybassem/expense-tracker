package com.myprojects.expense.reporter.controller;

import com.myprojects.expense.common.model.response.ErrorResponse;
import com.myprojects.expense.reporter.exception.ReportNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ReporterExceptionHandlers {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleReportNotFoundException(ReportNotFoundException exception) {
        return new ErrorResponse("Report for date (" + DATE_TIME_FORMATTER.format(exception.getDate())
                + ") is not found!");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValidException() {
        return new ErrorResponse("An invalid date has been requested!");
    }

    @ExceptionHandler(DateTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleDateTimeException() {
        return new ErrorResponse("An invalid date has been requested!");
    }

}
