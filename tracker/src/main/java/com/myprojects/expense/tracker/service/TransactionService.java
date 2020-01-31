package com.myprojects.expense.tracker.service;

import com.myprojects.expense.tracker.model.request.CreateTransactionRequest;
import com.myprojects.expense.tracker.model.request.UpdateTransactionRequest;
import com.myprojects.expense.tracker.model.response.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionResponse get(UUID id);

    List<TransactionResponse> getAll();

    void delete(UUID id);

    TransactionResponse create(CreateTransactionRequest request);

    TransactionResponse update(UUID id, UpdateTransactionRequest request);

}
