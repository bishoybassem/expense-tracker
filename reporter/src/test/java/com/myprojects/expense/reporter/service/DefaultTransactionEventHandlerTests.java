package com.myprojects.expense.reporter.service;

import com.myprojects.expense.messages.EventProtos.Event;
import com.myprojects.expense.messages.EventProtos.EventData;
import com.myprojects.expense.messages.EventProtos.EventType;
import com.myprojects.expense.reporter.config.ReporterServiceConfig;
import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportStats;
import com.myprojects.expense.reporter.model.ReportTransaction;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;

@ContextConfiguration(classes = ReporterServiceConfig.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class DefaultTransactionEventHandlerTests extends AbstractTestNGSpringContextTests {

    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final UUID TEST_OWNER_ID = UUID.randomUUID();
    private static final String TEST_DATE_FORMATTED = TEST_DATE.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    private static final BigDecimal FIVE = new BigDecimal("5");

    @MockBean
    private DayReportDao dayReportDao;

    @MockBean
    private ReportService reportService;

    @Autowired
    private DefaultTransactionEventHandler defaultTransactionEventHandler;

    @AfterMethod
    public void resetMocks() {
        Mockito.reset(dayReportDao);
    }

    @Test
    public void testCreateEventForExpenseTransaction() {
        mockInitialReportNoTransactions();
        DayReport report = handleEvent(newCreateEvent("some_id", false));
        assertThat(report.getIncomes(), hasSize(0));
        assertThat(report.getExpenses(), hasSize(1));
        assertThat(report.getExpenses().get(0).getId(), is("some_id"));
        assertThat(report.getExpenses().get(0).getAmount(), is(ONE));
        assertThat(report.getExpenses().get(0).getCategory(), is("abc"));
        assertThat(report.getStats().getTotal(), comparesEqualTo(ONE.negate()));
        assertThat(report.getStats().getTotalIncomes(), comparesEqualTo(ZERO));
        assertThat(report.getStats().getTotalExpenses(), comparesEqualTo(ONE));
    }

    @Test
    public void testCreateEventForIncomeTransaction() {
        mockInitialReportNoTransactions();
        DayReport report = handleEvent(newCreateEvent("some_id", true));
        assertThat(report.getIncomes(), hasSize(1));
        assertThat(report.getIncomes().get(0).getId(), is("some_id"));
        assertThat(report.getIncomes().get(0).getAmount(), is(ONE));
        assertThat(report.getIncomes().get(0).getCategory(), is("abc"));
        assertThat(report.getExpenses(), hasSize(0));
        assertThat(report.getStats().getTotal(), comparesEqualTo(ONE));
        assertThat(report.getStats().getTotalIncomes(), comparesEqualTo(ONE));
        assertThat(report.getStats().getTotalExpenses(), comparesEqualTo(ZERO));
    }

    @Test
    public void testCreateEventWithInitialReport() {
        mockInitialReport("income_id1", "expense_id1");
        DayReport report = handleEvent(newCreateEvent("income_id2", false));
        assertThat(report.getIncomes(), hasSize(1));
        assertThat(report.getExpenses(), hasSize(2));
        assertThat(report.getStats().getTotal(), comparesEqualTo(new BigDecimal("4")));
        assertThat(report.getStats().getTotalIncomes(), comparesEqualTo(TEN));
        assertThat(report.getStats().getTotalExpenses(), comparesEqualTo(new BigDecimal("6")));
    }

    @Test
    public void testDeleteEventForIncomeTransaction() {
        mockInitialReport("income_id", "expense_id");
        DayReport report = handleEvent(newDeleteEvent("income_id", true));
        assertThat(report.getIncomes(), hasSize(0));
        assertThat(report.getExpenses(), hasSize(1));
        assertThat(report.getStats().getTotal(), comparesEqualTo(FIVE.negate()));
        assertThat(report.getStats().getTotalIncomes(), comparesEqualTo(ZERO));
        assertThat(report.getStats().getTotalExpenses(), comparesEqualTo(FIVE));
    }

    @Test
    public void testDeleteEventForExpenseTransaction() {
        mockInitialReport("income_id", "expense_id");
        DayReport report = handleEvent(newDeleteEvent("expense_id", false));
        assertThat(report.getIncomes(), hasSize(1));
        assertThat(report.getExpenses(), hasSize(0));
        assertThat(report.getStats().getTotal(), comparesEqualTo(TEN));
        assertThat(report.getStats().getTotalIncomes(), comparesEqualTo(TEN));
        assertThat(report.getStats().getTotalExpenses(), comparesEqualTo(ZERO));
    }

    @Test
    public void testDeleteEventNoInitialReport() {
        mockInitialReportNoTransactions();
        DayReport report = handleEvent(newDeleteEvent("some_id", false));
        assertThat(report.getIncomes(), hasSize(0));
        assertThat(report.getExpenses(), hasSize(0));
        assertThat(report.getStats().getTotal(), comparesEqualTo(ZERO));
        assertThat(report.getStats().getTotalIncomes(), comparesEqualTo(ZERO));
        assertThat(report.getStats().getTotalExpenses(), comparesEqualTo(ZERO));
    }

    @Test
    public void testModifyEventForIncomeTransaction() {
        mockInitialReport("income_id", "expense_id");
        DayReport report = handleEvent(newModifyEvent("income_id", true));
        assertThat(report.getIncomes(), hasSize(1));
        assertThat(report.getExpenses(), hasSize(1));
        assertThat(report.getStats().getTotal(), comparesEqualTo(new BigDecimal("15")));
        assertThat(report.getStats().getTotalIncomes(), comparesEqualTo(new BigDecimal("20")));
        assertThat(report.getStats().getTotalExpenses(), comparesEqualTo(FIVE));
    }

    @Test
    public void testModifyEventForExpenseTransaction() {
        mockInitialReport("income_id", "expense_id");
        DayReport report = handleEvent(newModifyEvent("expense_id", false));
        assertThat(report.getIncomes(), hasSize(1));
        assertThat(report.getExpenses(), hasSize(1));
        assertThat(report.getStats().getTotal(), comparesEqualTo(TEN.negate()));
        assertThat(report.getStats().getTotalIncomes(), comparesEqualTo(TEN));
        assertThat(report.getStats().getTotalExpenses(), comparesEqualTo(new BigDecimal("20")));
    }

    private DayReport handleEvent(Event event) {
        defaultTransactionEventHandler.handleTransactionEvent(event);

        ArgumentCaptor<DayReport> requestCaptor = ArgumentCaptor.forClass(DayReport.class);

        Mockito.verify(dayReportDao, atLeast(1)).save(requestCaptor.capture());
        DayReport report = requestCaptor.getValue();
        assertThat(report, notNullValue());
        assertThat(report.getDate(), is(TEST_DATE));

        return report;
    }

    private void mockInitialReport(String incomeTransactionId, String expenseTransactionId) {
        ArrayList<ReportTransaction> incomes = new ArrayList<>();
        incomes.add(new ReportTransaction(incomeTransactionId, TEN, "abc"));

        ArrayList<ReportTransaction> expenses = new ArrayList<>();
        expenses.add(new ReportTransaction(expenseTransactionId, FIVE, "abc"));

        DayReport initialReport = new DayReport();
        initialReport.setDate(TEST_DATE);
        initialReport.setIncomes(incomes);
        initialReport.setExpenses(expenses);
        initialReport.setStats(new ReportStats(FIVE, TEN, FIVE));
        Mockito.when(reportService.getDayReportOrCreate(eq(TEST_DATE), eq(TEST_OWNER_ID)))
                .thenReturn(initialReport);
    }

    private void mockInitialReportNoTransactions() {
        DayReport emptyReport = new DayReport();
        emptyReport.setDate(TEST_DATE);
        emptyReport.setExpenses(new ArrayList<>());
        emptyReport.setIncomes(new ArrayList<>());
        emptyReport.setStats(new ReportStats(ZERO, ZERO, ZERO));
        Mockito.when(reportService.getDayReportOrCreate(eq(TEST_DATE), eq(TEST_OWNER_ID)))
                .thenReturn(DayReport.emptyReport(TEST_DATE, TEST_OWNER_ID));
    }

    private static Event newCreateEvent(String transactionId, boolean isIncome) {
        return Event.newBuilder()
                .setType(EventType.CREATE)
                .setTransactionId(transactionId)
                .setOwnerId(TEST_OWNER_ID.toString())
                .setTransactionType(isIncome)
                .setTransactionData(EventData.newBuilder()
                        .setAmount("1")
                        .setCategory("abc")
                        .setDate(TEST_DATE_FORMATTED))
                .build();
    }

    private static Event newDeleteEvent(String transactionId, boolean isIncome) {
        return Event.newBuilder()
                .setType(EventType.DELETE)
                .setTransactionId(transactionId)
                .setOwnerId(TEST_OWNER_ID.toString())
                .setTransactionType(isIncome)
                .setTransactionData(EventData.newBuilder()
                        .setCategory("abc")
                        .setDate(TEST_DATE_FORMATTED))
                .build();
    }

    private static Event newModifyEvent(String transactionId, boolean isIncome) {
        return Event.newBuilder()
                .setType(EventType.MODIFY)
                .setTransactionId(transactionId)
                .setOwnerId(TEST_OWNER_ID.toString())
                .setTransactionType(isIncome)
                .setTransactionData(EventData.newBuilder()
                        .setAmount("20")
                        .setCategory("abc")
                        .setDate(TEST_DATE_FORMATTED))
                .setOldTransactionData(EventData.newBuilder()
                        .setCategory("xyz")
                        .setDate(TEST_DATE_FORMATTED))
                .build();
    }

}