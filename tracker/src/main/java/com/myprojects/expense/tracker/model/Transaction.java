package com.myprojects.expense.tracker.model;

import com.myprojects.expense.tracker.dao.LocalDateJpaConverter;
import com.myprojects.expense.tracker.dao.TransactionTypeJpaConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transaction", schema = "expense_tracker_v1")
public class Transaction {

    @Id
    @GeneratedValue(generator = "uuid-generator")
    @GenericGenerator(name = "uuid-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "transaction_type")
    @Convert(converter = TransactionTypeJpaConverter.class)
    private TransactionType type;

    @Column
    private BigDecimal amount;

    @Column
    private String category;

    @Column(name = "transaction_date")
    @Convert(converter = LocalDateJpaConverter.class)
    private LocalDate date;

    @Column
    private String comment;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
