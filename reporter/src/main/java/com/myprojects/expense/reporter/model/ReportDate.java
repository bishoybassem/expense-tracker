package com.myprojects.expense.reporter.model;

import java.time.LocalDate;

public class ReportDate {

    private int day;
    private int month;
    private int year;

    public ReportDate() {

    }

    public ReportDate(LocalDate date) {
        this.day = date.getDayOfMonth();
        this.month = date.getMonthValue();
        this.year = date.getYear();
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

}
