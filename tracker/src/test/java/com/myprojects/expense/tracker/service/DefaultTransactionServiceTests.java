package com.myprojects.expense.tracker.service;

import com.myprojects.expense.tracker.config.ServiceConfig;
import com.myprojects.expense.tracker.dao.TransactionDao;
import com.myprojects.expense.tracker.exception.TransactionNotFoundException;
import com.myprojects.expense.tracker.model.Transaction;
import com.myprojects.expense.tracker.model.TransactionType;
import com.myprojects.expense.tracker.model.request.CreateTransactionRequest;
import com.myprojects.expense.tracker.model.request.UpdateTransactionRequest;
import com.myprojects.expense.tracker.model.response.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = ServiceConfig.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class DefaultTransactionServiceTests extends AbstractTestNGSpringContextTests {

    @MockBean
    private TransactionDao mockTransactionDao;

    @Autowired
    private TransactionService transactionService;

    @Test(expectedExceptions = TransactionNotFoundException.class)
    public void testGetNonExistentThrowsException() throws Exception {
        when(mockTransactionDao.findOne(any())).thenReturn(null);

        transactionService.get(randomUUID());
    }

    @Test(expectedExceptions = TransactionNotFoundException.class)
    public void testUpdateNonExistentThrowsException() throws Exception {
        when(mockTransactionDao.findOne(any())).thenReturn(null);

        transactionService.update(randomUUID(), new UpdateTransactionRequest());
    }

    @Test(expectedExceptions = TransactionNotFoundException.class)
    public void testDeleteNonExistentThrowsException() throws Exception {
        when(mockTransactionDao.findOne(any())).thenReturn(null);

        transactionService.delete(randomUUID());
    }

    @Test
    public void testCreatePassesAllFieldsCorrectly() throws Exception {
        when(mockTransactionDao.save(any(Transaction.class))).thenAnswer(returnsFirstArg());

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setType(TransactionType.EXPENSE);
        request.setAmount(new BigDecimal("1.23"));
        request.setCategory("abc");
        request.setDate(LocalDate.of(2018, Month.FEBRUARY, 13));
        request.setComment("comment");

        TransactionResponse transaction = transactionService.create(request);

        assertThat(transaction.getType(), is(TransactionType.EXPENSE));
        assertThat(transaction.getAmount(), is(new BigDecimal("1.23")));
        assertThat(transaction.getCategory(), is("abc"));
        assertThat(transaction.getDate(), is(LocalDate.of(2018, Month.FEBRUARY, 13)));
        assertThat(transaction.getComment(), is("comment"));
    }

    @Test
    public void testUpdatePassesAllFieldsCorrectly() throws Exception {
        when(mockTransactionDao.findOne(any())).thenReturn(new Transaction());
        when(mockTransactionDao.save(any(Transaction.class))).thenAnswer(returnsFirstArg());

        UpdateTransactionRequest request = new UpdateTransactionRequest();
        request.setAmount(new BigDecimal("1.23"));
        request.setCategory("abc");
        request.setDate(LocalDate.of(2018, Month.FEBRUARY, 13));
        request.setComment("comment");

        TransactionResponse transaction = transactionService.update(randomUUID(), request);

        assertThat(transaction.getAmount(), is(new BigDecimal("1.23")));
        assertThat(transaction.getCategory(), is("abc"));
        assertThat(transaction.getDate(), is(LocalDate.of(2018, Month.FEBRUARY, 13)));
        assertThat(transaction.getComment(), is("comment"));
    }
}
