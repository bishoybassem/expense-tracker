package com.myprojects.expense.reporter.model;

public class ReportDate {

    private int day;
    private int month;
    private int year;

    public ReportDate() {

    }

    public ReportDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
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
