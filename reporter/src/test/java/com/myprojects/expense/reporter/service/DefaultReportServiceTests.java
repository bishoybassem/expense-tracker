package com.myprojects.expense.reporter.service;


import com.myprojects.expense.reporter.config.ReporterServiceConfig;
import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.exception.ReportNotFoundException;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportStats;
import com.myprojects.expense.reporter.model.ReportTransaction;
import com.myprojects.expense.reporter.model.response.DayReportResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.data.domain.Example;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = ReporterServiceConfig.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class DefaultReportServiceTests extends AbstractTestNGSpringContextTests {

    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final UUID TEST_OWNER_ID = UUID.randomUUID();

    @MockBean
    private DayReportDao mockReportDao;

    @Autowired
    private DefaultReportService reportService;

    @BeforeClass
    public void setSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(TEST_OWNER_ID, null,
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    @AfterClass
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetDayReport() {
        DayReport report = new DayReport();
        report.setDate(TEST_DATE);
        report.setOwnerId(TEST_OWNER_ID);
        report.setStats(new ReportStats(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO));
        report.setIncomes(Arrays.asList(new ReportTransaction("tid", BigDecimal.TEN, "test")));
        report.setExpenses(emptyList());

        Mockito.when(mockReportDao.findOne(any()))
                .thenReturn(Optional.of(report));

        DayReportResponse response = reportService.getDayReport(TEST_DATE.getYear(), TEST_DATE.getMonthValue(),
                TEST_DATE.getDayOfMonth());

        assertThat(response.getDate(), is(report.getDate()));
        assertThat(response.getStats(), is(report.getStats()));
        assertThat(response.getIncomes(), is(report.getIncomes()));
        assertThat(response.getExpenses(), is(report.getExpenses()));

        ArgumentCaptor<Example<DayReport>> argumentCaptor = ArgumentCaptor.forClass(Example.class);
        Mockito.verify(mockReportDao).findOne(argumentCaptor.capture());

        LocalDate exampleDate = argumentCaptor.getValue().getProbe().getDate();
        assertThat(exampleDate, is(TEST_DATE));
    }

    @Test(expectedExceptions = ReportNotFoundException.class)
    public void testGetDayReportDoesNotExist() {
        Mockito.when(mockReportDao.findOne(any()))
                .thenReturn(Optional.empty());

        reportService.getDayReport(TEST_DATE.getYear(), TEST_DATE.getMonthValue(),
                TEST_DATE.getDayOfMonth());
    }

}