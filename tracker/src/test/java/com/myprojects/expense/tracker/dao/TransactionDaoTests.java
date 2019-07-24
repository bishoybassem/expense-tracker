package com.myprojects.expense.tracker.dao;

import com.myprojects.expense.tracker.config.TrackerDatabaseConfig;
import com.myprojects.expense.tracker.model.Transaction;
import com.myprojects.expense.tracker.model.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ContextConfiguration(classes = TrackerDatabaseConfig.class)
public class TransactionDaoTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private TransactionDao dao;

    private UUID createdTransactionId;

    @Test
    public void testCreate() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.EXPENSE);
        transaction.setAmount(new BigDecimal("1.23"));
        transaction.setCategory("abc");
        transaction.setDate(LocalDate.of(2018, Month.FEBRUARY, 13));
        transaction.setComment("comment");
        dao.save(transaction);

        assertThat(transaction.getId(), notNullValue());

        createdTransactionId = transaction.getId();
    }

    @Test(dependsOnMethods = "testCreate")
    public void testFind() throws Exception {
        Transaction transaction = dao.findById(createdTransactionId).get();

        assertThat(transaction.getType(), is(TransactionType.EXPENSE));
        assertThat(transaction.getAmount(), is(new BigDecimal("1.23")));
        assertThat(transaction.getCategory(), is("abc"));
        assertThat(transaction.getDate(), is(LocalDate.of(2018, Month.FEBRUARY, 13)));
        assertThat(transaction.getComment(), is("comment"));
    }

    @Test(dependsOnMethods = {"testCreate", "testFind"})
    public void testUpdate() throws Exception {
        Transaction transaction = dao.findById(createdTransactionId).get();
        transaction.setType(TransactionType.INCOME);
        transaction.setDate(LocalDate.of(1992, Month.MARCH, 9));
        dao.save(transaction);
        transaction = dao.findById(createdTransactionId).get();

        assertThat(transaction.getType(), is(TransactionType.INCOME));
        assertThat(transaction.getAmount(), is(new BigDecimal("1.23")));
        assertThat(transaction.getCategory(), is("abc"));
        assertThat(transaction.getDate(), is(LocalDate.of(1992, Month.MARCH, 9)));
        assertThat(transaction.getComment(), is("comment"));
    }

    @Test(dependsOnMethods = {"testCreate", "testFind", "testUpdate"})
    public void testDelete() throws Exception {
        dao.deleteById(createdTransactionId);

        assertThat(dao.findById(createdTransactionId).isPresent(), is(false));
    }

}
