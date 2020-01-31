package com.myprojects.expense.tracker.service;

import com.myprojects.expense.messages.EventProtos.Event;
import com.myprojects.expense.messages.EventProtos.EventData;
import com.myprojects.expense.messages.EventProtos.EventType;
import com.myprojects.expense.tracker.config.TrackerServiceConfig;
import com.myprojects.expense.tracker.dao.TransactionDao;
import com.myprojects.expense.tracker.exception.TransactionNotFoundException;
import com.myprojects.expense.tracker.model.Transaction;
import com.myprojects.expense.tracker.model.TransactionType;
import com.myprojects.expense.tracker.model.request.CreateTransactionRequest;
import com.myprojects.expense.tracker.model.request.UpdateTransactionRequest;
import com.myprojects.expense.tracker.model.response.TransactionResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = TrackerServiceConfig.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class DefaultTransactionServiceTests extends AbstractTestNGSpringContextTests {

    @MockBean
    private TransactionDao mockTransactionDao;

    @MockBean
    private RabbitTemplate mockRabbitTemplate;

    @Autowired
    private TransactionService transactionService;

    @BeforeMethod
    public void resetMocks() {
        Mockito.reset(mockTransactionDao);
        Mockito.reset(mockRabbitTemplate);
    }

    @Test(expectedExceptions = TransactionNotFoundException.class)
    public void testGetNonExistentThrowsException() {
        when(mockTransactionDao.findById(any())).thenReturn(Optional.empty());

        transactionService.get(randomUUID());
    }

    @Test(expectedExceptions = TransactionNotFoundException.class)
    public void testUpdateNonExistentThrowsException() {
        when(mockTransactionDao.findById(any())).thenReturn(Optional.empty());

        transactionService.update(randomUUID(), new UpdateTransactionRequest());
    }

    @Test(expectedExceptions = TransactionNotFoundException.class)
    public void testDeleteNonExistentThrowsException() {
        when(mockTransactionDao.findById(any())).thenReturn(Optional.empty());

        transactionService.delete(randomUUID());
    }

    @Test
    public void testGet() {
        Transaction transaction = createTestTransaction();
        when(mockTransactionDao.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        TransactionResponse response = transactionService.get(transaction.getId());
        assertTransactionResponse(response, transaction.getId());
    }

    @Test
    public void testGetAll() {
        Transaction transaction1 = createTestTransaction();
        Transaction transaction2 = createTestTransaction();
        when(mockTransactionDao.findAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        List<TransactionResponse> response = transactionService.getAll();
        assertThat(response, hasSize(2));
        assertTransactionResponse(response.get(0), transaction1.getId());
        assertTransactionResponse(response.get(1), transaction2.getId());
    }

    @Test
    public void testCreatePassesAllFieldsCorrectly() {
        final UUID transactionId = randomUUID();
        when(mockTransactionDao.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(transactionId);
            return transaction;
        });

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setType(TransactionType.EXPENSE.toString());
        request.setAmount(new BigDecimal("1.23"));
        request.setCategory("abc");
        request.setDate(LocalDate.of(2018, Month.FEBRUARY, 13));
        request.setComment("comment");

        TransactionResponse response = transactionService.create(request);
        assertTransactionResponse(response, transactionId);

        ArgumentCaptor<Event> requestCaptor = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(mockRabbitTemplate).convertAndSend(requestCaptor.capture());

        Event createEvent = requestCaptor.getValue();
        assertEvent(createEvent, EventType.CREATE, transactionId);
        assertEventData(createEvent.getTransactionData());
    }

    @Test
    public void testUpdatePassesAllFieldsCorrectly() {
        Transaction transaction = createTestTransaction();
        when(mockTransactionDao.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        when(mockTransactionDao.save(any(Transaction.class))).thenAnswer(returnsFirstArg());

        UpdateTransactionRequest request = new UpdateTransactionRequest();
        request.setAmount(new BigDecimal("2.34"));
        request.setCategory("xyz");
        request.setDate(LocalDate.of(2020, Month.MARCH, 9));
        request.setComment("another comment");

        TransactionResponse response = transactionService.update(transaction.getId(), request);
        assertThat(response.getId(), is(transaction.getId()));
        assertThat(response.getType(), is(TransactionType.EXPENSE));
        assertThat(response.getAmount(), is(new BigDecimal("2.34")));
        assertThat(response.getCategory(), is("xyz"));
        assertThat(response.getDate(), is(LocalDate.of(2020, Month.MARCH, 9)));
        assertThat(response.getComment(), is("another comment"));

        ArgumentCaptor<Event> requestCaptor = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(mockRabbitTemplate).convertAndSend(requestCaptor.capture());

        Event modifyEvent = requestCaptor.getValue();
        assertEvent(modifyEvent, EventType.MODIFY, transaction.getId());
        assertEventData(modifyEvent.getOldTransactionData());
        assertThat(modifyEvent.getTransactionData().getAmount(), is("2.34"));
        assertThat(modifyEvent.getTransactionData().getCategory(), is("xyz"));
        assertThat(modifyEvent.getTransactionData().getDate(), is("09/03/2020"));
    }

    @Test
    public void testDeleteSendsEventCorrectly() {
        Transaction transaction = createTestTransaction();
        when(mockTransactionDao.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        transactionService.delete(transaction.getId());

        ArgumentCaptor<Event> requestCaptor = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(mockRabbitTemplate).convertAndSend(requestCaptor.capture());

        Event deleteEvent = requestCaptor.getValue();
        assertEvent(deleteEvent, EventType.DELETE, transaction.getId());
        assertEventData(deleteEvent.getTransactionData());
    }


    private static Transaction createTestTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(randomUUID());
        transaction.setType(TransactionType.EXPENSE);
        transaction.setAmount(new BigDecimal("1.23"));
        transaction.setCategory("abc");
        transaction.setDate(LocalDate.of(2018, Month.FEBRUARY, 13));
        transaction.setComment("comment");
        return transaction;
    }

    private static void assertTransactionResponse(TransactionResponse response, UUID transactionUUID) {
        assertThat(response.getId(), is(transactionUUID));
        assertThat(response.getType(), is(TransactionType.EXPENSE));
        assertThat(response.getAmount(), is(new BigDecimal("1.23")));
        assertThat(response.getCategory(), is("abc"));
        assertThat(response.getDate(), is(LocalDate.of(2018, Month.FEBRUARY, 13)));
        assertThat(response.getComment(), is("comment"));
    }

    private static void assertEvent(Event event, EventType type, UUID transactionId) {
        assertThat(event, notNullValue());
        assertThat(event.getType(), is(type));
        assertThat(event.getTransactionId(), is(transactionId.toString()));
    }

    private static void assertEventData(EventData eventData) {
        assertThat(eventData.getAmount(), is("1.23"));
        assertThat(eventData.getCategory(), is("abc"));
        assertThat(eventData.getDate(), is("13/02/2018"));
    }
}
