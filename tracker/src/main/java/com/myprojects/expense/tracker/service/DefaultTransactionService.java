package com.myprojects.expense.tracker.service;

import com.google.common.collect.Streams;
import com.myprojects.expense.tracker.dao.TransactionDao;
import com.myprojects.expense.tracker.exception.TransactionNotFoundException;
import com.myprojects.expense.tracker.model.Transaction;
import com.myprojects.expense.tracker.model.event.CreateEvent;
import com.myprojects.expense.tracker.model.event.DeleteEvent;
import com.myprojects.expense.tracker.model.event.EventData;
import com.myprojects.expense.tracker.model.event.ModifyEvent;
import com.myprojects.expense.tracker.model.request.CreateTransactionRequest;
import com.myprojects.expense.tracker.model.request.UpdateTransactionRequest;
import com.myprojects.expense.tracker.model.response.TransactionResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DefaultTransactionService implements TransactionService {

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

        DeleteEvent deleteEvent = new DeleteEvent(id);
        deleteEvent.setTransactionData(createEventData(transaction));
        rabbitTemplate.convertAndSend(deleteEvent);

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

        CreateEvent createEvent = new CreateEvent(transaction.getId());
        createEvent.setTransactionData(createEventData(transaction));
        rabbitTemplate.convertAndSend(createEvent);

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

        ModifyEvent modifyEvent = new ModifyEvent(transaction.getId());
        modifyEvent.setTransactionData(oldTransactionData);
        modifyEvent.setNewTransactionData(createEventData(transaction));
        rabbitTemplate.convertAndSend(modifyEvent);

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

    private static EventData createEventData(Transaction transaction) {
        EventData eventData = new EventData();
        eventData.setAmount(transaction.getAmount());
        eventData.setCategory(transaction.getCategory());
        eventData.setDate(transaction.getDate());
        return eventData;
    }

}
