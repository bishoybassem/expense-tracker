package com.myprojects.expense.reporter.dao;


import com.myprojects.expense.reporter.config.ReporterDatabaseConfig;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportStats;
import com.myprojects.expense.reporter.model.ReportTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DataMongoTest
@ContextConfiguration(classes = ReporterDatabaseConfig.class)
public class DayReportDaoIntegrationTests extends AbstractTestNGSpringContextTests {

    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final UUID TEST_OWNER_ID = UUID.randomUUID();

    @Autowired
    private DayReportDao dayReportDao;

    @BeforeMethod
    public void cleanup() {
        dayReportDao.deleteAll();
    }

    @Test
    public void testCreate() {
        String createdReportId = createReport();

        assertThat(createdReportId, not(emptyString()));
    }

    @Test
    public void testFindOne() {
        String createdReportId = createReport();

        DayReport reportProbe = new DayReport();
        reportProbe.setDate(TEST_DATE);
        reportProbe.setOwnerId(TEST_OWNER_ID);

        DayReport report = dayReportDao.findOne(Example.of(reportProbe)).get();

        assertThat(report.getId(), is(createdReportId));
        assertThat(report.getDate(), is(TEST_DATE));
        assertThat(report.getOwnerId(), is(TEST_OWNER_ID));
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

    private String createReport() {
        ArrayList<ReportTransaction> incomes = new ArrayList<>();
        incomes.add(new ReportTransaction("id1", TEN, "abc"));

        ArrayList<ReportTransaction> expenses = new ArrayList<>();
        expenses.add(new ReportTransaction("id2", TEN, "xyz"));

        DayReport report = new DayReport();
        report.setOwnerId(TEST_OWNER_ID);
        report.setDate(TEST_DATE);
        report.setIncomes(incomes);
        report.setExpenses(expenses);
        report.setStats(new ReportStats(ZERO, TEN, TEN));

        return dayReportDao.save(report).getId();
    }

}