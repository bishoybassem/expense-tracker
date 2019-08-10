package com.myprojects.expense.tracker.controller;

import com.myprojects.expense.tracker.model.request.CreateTransactionRequest;
import com.myprojects.expense.tracker.model.request.UpdateTransactionRequest;
import com.myprojects.expense.tracker.model.response.TransactionResponse;
import com.myprojects.expense.tracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TransactionController.PATH)
public class TransactionController {

    public static final String PATH = "/v1/transactions";

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/{id}")
    public TransactionResponse get(@PathVariable UUID id) {
        return transactionService.get(id);
    }

    @GetMapping
    public List<TransactionResponse> getAll() {
        return transactionService.getAll();
    }

    @DeleteMapping("/{id}")
    public TransactionResponse delete(@PathVariable UUID id) {
        return transactionService.delete(id);
    }

    @PostMapping
    public TransactionResponse create(@RequestBody CreateTransactionRequest request) {
        return transactionService.create(request);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable UUID id, @RequestBody UpdateTransactionRequest request) {
        return transactionService.update(id, request);
    }

}
