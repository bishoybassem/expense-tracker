package com.myprojects.expense.reporter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReportNotFoundException extends RuntimeException {

    private LocalDate date;

    public ReportNotFoundException(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }
}
