package com.myprojects.expense.reporter.dao;


import com.myprojects.expense.reporter.config.ReporterDatabaseConfig;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportDate;
import com.myprojects.expense.reporter.model.ReportStats;
import com.myprojects.expense.reporter.model.ReportTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;

@DataMongoTest
@ContextConfiguration(classes = ReporterDatabaseConfig.class)
public class DayReportDaoTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private DayReportDao dayReportDao;

    private String createdReportId;

    @BeforeClass
    public void cleanup() {
        dayReportDao.deleteAll();
    }

    @Test
    public void testCreate() throws Exception {
        ArrayList<ReportTransaction> incomes = new ArrayList<>();
        incomes.add(new ReportTransaction("id1", TEN, "abc"));

        ArrayList<ReportTransaction> expenses = new ArrayList<>();
        expenses.add(new ReportTransaction("id2", TEN, "xyz"));

        DayReport report = new DayReport();
        report.setDate(new ReportDate(2000, 12, 1));
        report.setIncomes(incomes);
        report.setExpenses(expenses);
        report.setStats(new ReportStats(ZERO, TEN, TEN));

        createdReportId = dayReportDao.save(report).getId();

        assertThat(createdReportId, not(isEmptyString()));
    }

    @Test(dependsOnMethods = "testCreate")
    public void testFindOne() throws Exception {
        DayReport reportProbe = new DayReport();
        reportProbe.setDate(new ReportDate(2000, 12, 1));

        DayReport report = dayReportDao.findOne(Example.of(reportProbe)).get();

        assertThat(report.getId(), is(createdReportId));
        assertThat(report.getDate().getDay(), is(1));
        assertThat(report.getDate().getMonth(), is(12));
        assertThat(report.getDate().getYear(), is(2000));
        assertThat(report.getIncomes(), hasSize(1));
        assertThat(report.getIncomes().get(0).getId(), is("id1"));
        assertThat(report.getIncomes().get(0).getAmount(), is(TEN));
        assertThat(report.getIncomes().get(0).getCategory(), is("abc"));
        assertThat(report.getExpenses(), hasSize(1));
        assertThat(report.getExpenses().get(0).getId(), is("id2"));
        assertThat(report.getExpenses().get(0).getAmount(), is(TEN));
        assertThat(report.getExpenses().get(0).getCategory(), is("xyz"));
        assertThat(report.getStats().getTotal(), is(ZERO));
        assertThat(report.getStats().getTotalExpenses(), is(TEN));
        assertThat(report.getStats().getTotalIncomes(), is(TEN));
    }
}