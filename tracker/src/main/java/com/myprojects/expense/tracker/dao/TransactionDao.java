package com.myprojects.expense.tracker.dao;

import com.myprojects.expense.tracker.model.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionDao extends CrudRepository<Transaction, UUID> {

    Optional<Transaction> findByIdAndOwnerId(UUID id, UUID ownerId);

    List<Transaction> findAllByOwnerId(UUID ownerId);

}
