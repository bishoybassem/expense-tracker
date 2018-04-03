package com.myprojects.expense.reporter.service;


import com.myprojects.expense.reporter.config.ReporterServiceConfig;
import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportDate;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = ReporterServiceConfig.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class DefaultReportServiceTests extends AbstractTestNGSpringContextTests {

    @MockBean
    private DayReportDao mockReportDao;

    @Autowired
    private DefaultReportService reportService;

    @Test
    public void testGetReportByDate() throws Exception {
        DayReport expectedReport = new DayReport();

        Mockito.when(mockReportDao.findOne(any()))
                .thenReturn(Optional.of(expectedReport));

        DayReport report = reportService.getDayReport(2018, 12, 1);
        assertThat(report, is(expectedReport));

        ArgumentCaptor<Example<DayReport>> argumentCaptor = ArgumentCaptor.forClass(Example.class);
        Mockito.verify(mockReportDao).findOne(argumentCaptor.capture());
        ReportDate exampleDate = argumentCaptor.getValue().getProbe().getDate();
        assertThat(exampleDate.getYear(), is(2018));
        assertThat(exampleDate.getMonth(), is(12));
        assertThat(exampleDate.getDay(), is(1));
    }

    @Test
    public void testGetReportCreatesEmptyReportIfItDoesNotExist() throws Exception {
        Mockito.when(mockReportDao.findOne(any()))
                .thenReturn(Optional.empty());

        Mockito.when(mockReportDao.save(any()))
                .thenAnswer(returnsFirstArg());

        DayReport report = reportService.getDayReport(2018, 12, 1);
        assertThat(report, notNullValue());
        assertThat(report.getDate().getYear(), is(2018));
        assertThat(report.getDate().getMonth(), is(12));
        assertThat(report.getDate().getDay(), is(1));
        assertThat(report.getStats().getTotal(), is(ZERO));
        assertThat(report.getStats().getTotalIncomes(), is(ZERO));
        assertThat(report.getStats().getTotalExpenses(), is(ZERO));
        assertThat(report.getIncomes(), empty());
        assertThat(report.getExpenses(), empty());

        Mockito.verify(mockReportDao).save(any());
    }

}