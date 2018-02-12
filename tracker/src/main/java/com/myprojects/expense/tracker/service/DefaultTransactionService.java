package com.myprojects.expense.tracker.service;

import com.google.common.collect.Streams;
import com.myprojects.expense.tracker.dao.TransactionDao;
import com.myprojects.expense.tracker.exception.TransactionNotFoundException;
import com.myprojects.expense.tracker.model.Transaction;
import com.myprojects.expense.tracker.model.request.CreateTransactionRequest;
import com.myprojects.expense.tracker.model.request.UpdateTransactionRequest;
import com.myprojects.expense.tracker.model.response.TransactionResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DefaultTransactionService implements TransactionService {

    private final TransactionDao transactionDao;

    public DefaultTransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
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
        return createResponse(transaction);
    }

    @Override
    public TransactionResponse update(UUID id, UpdateTransactionRequest request) {
        Transaction transaction = getTransaction(id);
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setComment(request.getComment());
        transaction = transactionDao.save(transaction);
        return createResponse(transaction);
    }

    private Transaction getTransaction(UUID id) {
        Transaction transaction = transactionDao.findOne(id);
        if (transaction == null) {
            throw new TransactionNotFoundException();
        }
        return transaction;
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

}
