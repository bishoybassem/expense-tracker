package com.myprojects.expense.tracker.dao;

import com.myprojects.expense.tracker.model.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TransactionDao extends CrudRepository<Transaction, UUID> {

}
