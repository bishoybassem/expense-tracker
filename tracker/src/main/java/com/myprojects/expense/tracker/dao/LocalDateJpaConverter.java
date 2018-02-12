package com.myprojects.expense.tracker.dao;

import javax.persistence.AttributeConverter;
import java.sql.Date;
import java.time.LocalDate;

public class LocalDateJpaConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDate localDate) {
        return Date.valueOf(localDate);
    }

    @Override
    public LocalDate convertToEntityAttribute(Date date) {
        return date.toLocalDate();
    }
}
