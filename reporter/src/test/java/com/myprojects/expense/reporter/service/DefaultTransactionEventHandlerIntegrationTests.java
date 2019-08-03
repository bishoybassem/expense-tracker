package com.myprojects.expense.reporter.service;

import com.myprojects.expense.messages.EventProtos.Event;
import com.myprojects.expense.messages.EventProtos.EventData;
import com.myprojects.expense.messages.EventProtos.EventType;
import com.myprojects.expense.reporter.config.ReporterDatabaseConfig;
import com.myprojects.expense.reporter.config.ReporterServiceConfig;
import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.model.DayReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@DataMongoTest
@ContextConfiguration(classes = { ReporterServiceConfig.class, ReporterDatabaseConfig.class })
public class DefaultTransactionEventHandlerIntegrationTests extends AbstractTestNGSpringContextTests {

    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final String TEST_DATE_FORMATTED = TEST_DATE.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));


    @Autowired
    private DefaultTransactionEventHandler transactionEventHandler;

    @Autowired
    private ReportService reportService;

    @Autowired
    private DayReportDao dayReportDao;

    @BeforeMethod
    public void setUp() {
        dayReportDao.deleteAll();
    }

    @Test
    public void testReportUpdateSynchronization() throws Exception {
        final int events = 50;
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);
        final Callable<Void> callable = () -> {
            transactionEventHandler.handleTransactionEvent(newCreateEvent());
            return null;
        };
        List<Future<Void>> results = threadPoolExecutor.invokeAll(Collections.nCopies(events, callable), 5000, TimeUnit.MILLISECONDS);
        for(Future<Void> result : results) {
            result.get();
        }
        DayReport report = reportService.getDayReport(TEST_DATE);
        assertThat(report.getStats().getTotal(), is(BigDecimal.valueOf(events)));
        assertThat(report.getIncomes(), hasSize(events));
    }

    private static Event newCreateEvent() {
        return Event.newBuilder()
                .setType(EventType.CREATE)
                .setTransactionId(UUID.randomUUID().toString())
                .setTransactionType(true)
                .setTransactionData(EventData.newBuilder()
                        .setAmount("1")
                        .setCategory("abc")
                        .setDate(TEST_DATE_FORMATTED))
                .build();
    }
}