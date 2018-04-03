package com.myprojects.expense.reporter.service;

import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportDate;
import com.myprojects.expense.reporter.model.ReportStats;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class DefaultReportService implements ReportService {

    private final DayReportDao dayReportDao;

    public DefaultReportService(DayReportDao dayReportDao) {
        this.dayReportDao = dayReportDao;
    }

    @Override
    public DayReport getDayReport(int year, int month, int day) {
        DayReport dayReportProbe = new DayReport();
        dayReportProbe.setDate(new ReportDate(year, month, day));
        Optional<DayReport> dayReport = dayReportDao.findOne(Example.of(dayReportProbe));
        return dayReport.orElseGet(() -> {
            dayReportProbe.setStats(new ReportStats(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
            dayReportProbe.setIncomes(new ArrayList<>());
            dayReportProbe.setExpenses(new ArrayList<>());
            return dayReportDao.save(dayReportProbe);
        });
    }

}
