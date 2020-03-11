package com.myprojects.expense.reporter.service;

import com.myprojects.expense.messages.EventProtos;
import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportStats;
import com.myprojects.expense.reporter.model.ReportTransaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultTransactionEventHandler implements TransactionEventHandler {

    private static final Log LOGGER = LogFactory.getLog(DefaultTransactionEventHandler.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DayReportDao dayReportDao;
    private final ReportService reportService;

    public DefaultTransactionEventHandler(DayReportDao dayReportDao, ReportService reportService) {
        this.dayReportDao = dayReportDao;
        this.reportService = reportService;
    }

    /**
     * Invoked when a message containing an {@link EventProtos.Event} is consumed from the message queue.
     * Given an event, this method queries the user's day report based on the event's date and owner id,
     * delegates the update logic to {@link #handleCreateEvent} and {@link #handleDeleteEvent} methods,
     * and finally saves the result to the database.
     *
     * Moreover, the updates to the reports are synchronized among other threads or reporter instances using
     * optimistic locking, and this method would take care of retrying the update logic in case of concurrent
     * modifications done to the same report.
     */
    @Override
    public void handleTransactionEvent(EventProtos.Event event) {
        retryInCaseOfOptimisticLockingFailure(() -> {
            LOGGER.info("Handling the following event:\n" + event.toString());
            DayReport dayReport = getDayReport(event.getTransactionData().getDate(), event.getOwnerId());
            if (event.getType() == EventProtos.EventType.CREATE) {
                handleCreateEvent(event, dayReport);
            } else if (event.getType() == EventProtos.EventType.DELETE) {
                handleDeleteEvent(event, dayReport);
            } else {
                handleDeleteEvent(event, dayReport);
                handleCreateEvent(event, dayReport);
            }
            dayReportDao.save(dayReport);
        });
    }

    /**
     * Given a transaction creation event and the corresponding user's day report, this method adds the new
     * transaction to the report and updates the report's stats accordingly.
     */
    private void handleCreateEvent(EventProtos.Event event, DayReport dayReport) {
        ReportTransaction transaction = new ReportTransaction();
        transaction.setId(event.getTransactionId());
        transaction.setAmount(new BigDecimal(event.getTransactionData().getAmount()));
        transaction.setCategory(event.getTransactionData().getCategory());
        ReportStats stats = dayReport.getStats();
        if (event.getTransactionType()) {
            dayReport.getIncomes().add(transaction);
            stats.setTotalIncomes(stats.getTotalIncomes().add(transaction.getAmount()));
        } else {
            dayReport.getExpenses().add(transaction);
            stats.setTotalExpenses(stats.getTotalExpenses().add(transaction.getAmount()));
        }
        updateTotal(dayReport.getStats());
    }

    /**
     * Given a transaction deletion event and the corresponding user's day report, this method deletes the
     * transaction from the report and updates the report's stats accordingly.
     */
    private void handleDeleteEvent(EventProtos.Event event, DayReport dayReport) {
        ReportStats stats = dayReport.getStats();
        if (event.getTransactionType()) {
            BigDecimal transactionAmount = removeTransactionFromList(event.getTransactionId(),
                    dayReport.getIncomes());
            stats.setTotalIncomes(stats.getTotalIncomes().subtract(transactionAmount));
        } else {
            BigDecimal transactionAmount = removeTransactionFromList(event.getTransactionId(),
                    dayReport.getExpenses());
            stats.setTotalExpenses(stats.getTotalExpenses().subtract(transactionAmount));
        }
        updateTotal(dayReport.getStats());
    }

    private DayReport getDayReport(String date, String ownerId) {
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        return reportService.getDayReportOrCreate(localDate, UUID.fromString(ownerId));
    }

    private static void updateTotal(ReportStats stats) {
        stats.setTotal(stats.getTotalIncomes().subtract(stats.getTotalExpenses()));
    }

    private static BigDecimal removeTransactionFromList(String transactionId, List<ReportTransaction> transactions) {
        Optional<ReportTransaction> transactionToBeRemoved = transactions.stream()
                .filter(transaction -> transaction.getId().equals(transactionId))
                .findFirst();

        if (transactionToBeRemoved.isPresent()) {
            transactions.remove(transactionToBeRemoved.get());
            return transactionToBeRemoved.get().getAmount();
        }
        return BigDecimal.ZERO;
    }

    private static void retryInCaseOfOptimisticLockingFailure(Runnable logic) {
        while (true) {
            try {
                logic.run();
                break;
            } catch (OptimisticLockingFailureException ex) {
                // The report was modified by another thread/reporter instance
            }
        }
    }
}
