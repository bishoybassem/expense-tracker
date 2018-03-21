package com.myprojects.expense.reporter.service;

import com.myprojects.expense.messages.EventProtos;
import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportDate;
import com.myprojects.expense.reporter.model.ReportStats;
import com.myprojects.expense.reporter.model.ReportTransaction;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultTransactionEventHandler implements TransactionEventHandler {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DayReportDao dayReportDao;

    public DefaultTransactionEventHandler(DayReportDao dayReportDao) {
        this.dayReportDao = dayReportDao;
    }

    @Override
    public void handleTransactionEvent(EventProtos.Event event) {
        if (event.getType() == EventProtos.EventType.CREATE) {
            handleCreateEvent(event.getTransactionId(), event.getTransactionType(), event.getTransactionData());
        } else if (event.getType() == EventProtos.EventType.DELETE) {
            handleDeleteEvent(event.getTransactionId(), event.getTransactionType(), event.getTransactionData());
        } else {
            handleDeleteEvent(event.getTransactionId(), event.getTransactionType(), event.getOldTransactionData());
            handleCreateEvent(event.getTransactionId(), event.getTransactionType(), event.getTransactionData());
        }
    }

    private void handleCreateEvent(String transactionId, boolean transactionType, EventProtos.EventData transactionData) {
        DayReport dayReport = findOrCreateReportByDate(transactionData.getDate());

        ReportTransaction transaction = new ReportTransaction();
        transaction.setId(transactionId);
        transaction.setAmount(new BigDecimal(transactionData.getAmount()));
        transaction.setCategory(transactionData.getCategory());

        ReportStats stats = dayReport.getStats();
        if (transactionType) {
            dayReport.getIncomes().add(transaction);
            stats.setTotalIncomes(stats.getTotalIncomes().add(transaction.getAmount()));
        } else {
            dayReport.getExpenses().add(transaction);
            stats.setTotalExpenses(stats.getTotalExpenses().add(transaction.getAmount()));
        }
        updateTotal(dayReport.getStats());

        dayReportDao.save(dayReport);
    }

    private void handleDeleteEvent(String transactionId, boolean transactionType, EventProtos.EventData transactionData) {
        DayReport dayReport = findOrCreateReportByDate(transactionData.getDate());

        ReportStats stats = dayReport.getStats();
        if (transactionType) {
            BigDecimal transactionAmount = removeTransactionFromList(transactionId, dayReport.getIncomes());
            stats.setTotalIncomes(stats.getTotalIncomes().subtract(transactionAmount));
        } else {
            BigDecimal transactionAmount = removeTransactionFromList(transactionId, dayReport.getExpenses());
            stats.setTotalExpenses(stats.getTotalExpenses().subtract(transactionAmount));
        }
        updateTotal(dayReport.getStats());

        dayReportDao.save(dayReport);
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

    private DayReport findOrCreateReportByDate(String date) {
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        DayReport dayReportProbe = new DayReport();
        dayReportProbe.setDate(new ReportDate(localDate));
        Optional<DayReport> dayReport = dayReportDao.findOne(Example.of(dayReportProbe));
        return dayReport.orElseGet(() -> {
            dayReportProbe.setStats(new ReportStats(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
            dayReportProbe.setIncomes(new ArrayList<>());
            dayReportProbe.setExpenses(new ArrayList<>());
            return dayReportDao.save(dayReportProbe);
        });
    }

}
