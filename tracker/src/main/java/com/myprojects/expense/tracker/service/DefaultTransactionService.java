package com.myprojects.expense.tracker.service;

import com.google.common.collect.Streams;
import com.myprojects.expense.messages.EventProtos.Event;
import com.myprojects.expense.messages.EventProtos.EventData;
import com.myprojects.expense.messages.EventProtos.EventType;
import com.myprojects.expense.tracker.dao.TransactionDao;
import com.myprojects.expense.tracker.exception.TransactionNotFoundException;
import com.myprojects.expense.tracker.model.Transaction;
import com.myprojects.expense.tracker.model.request.CreateTransactionRequest;
import com.myprojects.expense.tracker.model.request.UpdateTransactionRequest;
import com.myprojects.expense.tracker.model.response.TransactionResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DefaultTransactionService implements TransactionService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final TransactionDao transactionDao;
    private final RabbitTemplate rabbitTemplate;

    public DefaultTransactionService(TransactionDao transactionDao, RabbitTemplate rabbitTemplate) {
        this.transactionDao = transactionDao;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public TransactionResponse get(UUID id) {
        Transaction transaction = getTransaction(id);
        return createResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getAll() {
        return Streams.stream(transactionDao.findAll())
                .map(DefaultTransactionService::createResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse delete(UUID id) {
        Transaction transaction = getTransaction(id);
        transactionDao.delete(transaction);

        rabbitTemplate.convertAndSend(createEvent(EventType.DELETE, transaction, null));

        return createResponse(transaction);
    }

    @Override
    public TransactionResponse create(CreateTransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setComment(request.getComment());
        transaction = transactionDao.save(transaction);

        rabbitTemplate.convertAndSend(createEvent(EventType.CREATE, transaction, null));

        return createResponse(transaction);
    }

    @Override
    public TransactionResponse update(UUID id, UpdateTransactionRequest request) {
        Transaction transaction = getTransaction(id);

        EventData oldTransactionData = createEventData(transaction);

        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setComment(request.getComment());
        transaction = transactionDao.save(transaction);

        rabbitTemplate.convertAndSend(createEvent(EventType.MODIFY, transaction, oldTransactionData));

        return createResponse(transaction);
    }

    private Transaction getTransaction(UUID id) {
        Optional<Transaction> transaction = transactionDao.findById(id);
        if (!transaction.isPresent()) {
            throw new TransactionNotFoundException();
        }
        return transaction.get();
    }

    private static TransactionResponse createResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount());
        response.setCategory(transaction.getCategory());
        response.setDate(transaction.getDate());
        response.setComment(transaction.getComment());
        return response;
    }

    private static Event createEvent(EventType eventType, Transaction transaction,
                                     EventData oldTransactionData) {
        return Event.newBuilder()
                .setType(eventType)
                .setTransactionId(transaction.getId().toString())
                .setTransactionType(transaction.getType().getBooleanValue())
                .setTransactionData(createEventData(transaction))
                .setOldTransactionData(oldTransactionData == null ?
                        EventData.newBuilder().build() : oldTransactionData)
                .build();
    }

    private static EventData createEventData(Transaction transaction) {
        return EventData.newBuilder()
                .setAmount(transaction.getAmount().toString())
                .setCategory(transaction.getCategory())
                .setDate(DATE_FORMATTER.format(transaction.getDate()))
                .build();
    }

}
