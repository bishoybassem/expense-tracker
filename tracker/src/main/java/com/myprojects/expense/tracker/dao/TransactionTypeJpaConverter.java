package com.myprojects.expense.tracker.dao;

import com.myprojects.expense.tracker.model.TransactionType;

import javax.persistence.AttributeConverter;

public class TransactionTypeJpaConverter implements AttributeConverter<TransactionType, Boolean> {

    @Override
    public Boolean convertToDatabaseColumn(TransactionType type) {
        return type.getBooleanValue();
    }

    @Override
    public TransactionType convertToEntityAttribute(Boolean booleanValue) {
        if (TransactionType.INCOME.getBooleanValue() == booleanValue) {
            return TransactionType.INCOME;
        }
        return TransactionType.EXPENSE;
    }

}
