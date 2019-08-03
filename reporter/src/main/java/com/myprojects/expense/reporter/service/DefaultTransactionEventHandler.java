package com.myprojects.expense.reporter.service;

import com.myprojects.expense.messages.EventProtos;
import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportStats;
import com.myprojects.expense.reporter.model.ReportTransaction;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultTransactionEventHandler implements TransactionEventHandler {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DayReportDao dayReportDao;
    private final ReportService reportService;

    public DefaultTransactionEventHandler(DayReportDao dayReportDao, ReportService reportService) {
        this.dayReportDao = dayReportDao;
        this.reportService = reportService;
    }

    @Override
    public void handleTransactionEvent(EventProtos.Event event) {
        retryInCaseOfOptimisticLockingFailure(() -> {
            DayReport dayReport = getDayReport(event.getTransactionData().getDate());
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

    private DayReport getDayReport(String date) {
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        return reportService.getDayReportOrCreate(localDate);
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
        boolean failed = true;
        do {
            try {
                logic.run();
                failed = false;
            } catch (OptimisticLockingFailureException ex) {
                // The report was modified by another process/thread
            }
        } while (failed);
    }
}
