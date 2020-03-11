package com.myprojects.expense.tracker.service;

import com.myprojects.expense.messages.EventProtos.Event;
import com.myprojects.expense.messages.EventProtos.EventData;
import com.myprojects.expense.messages.EventProtos.EventType;
import com.myprojects.expense.tracker.dao.TransactionDao;
import com.myprojects.expense.tracker.exception.TransactionNotFoundException;
import com.myprojects.expense.tracker.model.Transaction;
import com.myprojects.expense.tracker.model.TransactionType;
import com.myprojects.expense.tracker.model.request.CreateTransactionRequest;
import com.myprojects.expense.tracker.model.request.UpdateTransactionRequest;
import com.myprojects.expense.tracker.model.response.TransactionResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DefaultTransactionService implements TransactionService {

    private static final Log LOGGER = LogFactory.getLog(DefaultTransactionService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final TransactionDao transactionDao;
    private final RabbitTemplate rabbitTemplate;

    public DefaultTransactionService(TransactionDao transactionDao, RabbitTemplate rabbitTemplate) {
        this.transactionDao = transactionDao;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Finds the transaction owned by the authenticated user and with the given id. If found, it populates and returns
     * a {@link TransactionResponse} with the transaction's details.
     *
     * If the transaction is not found, it throws a {@link TransactionNotFoundException}.
     */
    @Override
    public TransactionResponse get(UUID id) {
        Transaction transaction = getTransaction(id);
        return createResponse(transaction);
    }

    /**
     * Queries all transactions owned by the authenticated user, and returns the results as a list of
     * {@link TransactionResponse}s.
     */
    @Override
    public List<TransactionResponse> getAll() {
        return transactionDao.findAllByOwnerId(getLoggedInUserId()).stream()
                .map(DefaultTransactionService::createResponse)
                .collect(Collectors.toList());
    }

    /**
     * Deletes the transaction owned by the authenticated user and with the given id. If found, it posts a transaction
     * deletion event to the message queue.
     *
     * If the transaction is not found, it throws a {@link TransactionNotFoundException}.
     */
    @Override
    public void delete(UUID id) {
        Transaction transaction = getTransaction(id);
        transactionDao.delete(transaction);

        sendEvent(createEvent(EventType.DELETE, transaction, null));
    }

    /**
     * Creates a new transaction owned by the authenticated user and with the given {@link CreateTransactionRequest}
     * details. If successful, it posts a transaction creation event to the message queue, and returns a
     * {@link TransactionResponse} with the transaction's details.
     */
    @Override
    public TransactionResponse create(CreateTransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setOwnerId(getLoggedInUserId());
        transaction.setType(TransactionType.valueOf(request.getType()));
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setComment(request.getComment());
        transaction = transactionDao.save(transaction);

        sendEvent(createEvent(EventType.CREATE, transaction, null));

        return createResponse(transaction);
    }

    /**
     * Modifies the transaction owned by the authenticated user and with the given id. If found, It sets the
     * transaction's details to the {@link UpdateTransactionRequest} details, posts a transaction modification
     * event to the message queue, and returns a {@link TransactionResponse} with the transaction's details.
     *
     * If the transaction is not found, it throws a {@link TransactionNotFoundException}.
     */
    @Override
    public TransactionResponse update(UUID id, UpdateTransactionRequest request) {
        Transaction transaction = getTransaction(id);

        EventData oldTransactionData = createEventData(transaction);

        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setComment(request.getComment());
        transaction = transactionDao.save(transaction);

        sendEvent(createEvent(EventType.MODIFY, transaction, oldTransactionData));

        return createResponse(transaction);
    }

    private Transaction getTransaction(UUID id) {
        UUID ownerId = getLoggedInUserId();
        Optional<Transaction> transaction = transactionDao.findByIdAndOwnerId(id, ownerId);
        if (!transaction.isPresent()) {
            LOGGER.info("Transaction with id " + id + " is not found!");
            throw new TransactionNotFoundException(id);
        }
        return transaction.get();
    }

    private void sendEvent(Event event) {
        LOGGER.info("Sending the following event to the message queue:\n" + event.toString());
        rabbitTemplate.convertAndSend(event);
    }

    private static UUID getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UUID) authentication.getPrincipal();
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
                .setOwnerId(transaction.getOwnerId().toString())
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
