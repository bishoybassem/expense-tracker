package com.myprojects.expense.blackbox.test;

import com.myprojects.expense.blackbox.util.TransactionActions;
import com.myprojects.expense.blackbox.util.UserActions;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;

public class TransactionTests {

    private static final LocalDate TEST_DATE = LocalDate.now();

    @Test
    public void testTransactionIsOnlyVisibleToOwningUser() {
        String user1accessToken = UserActions.randomUser().signUpAndReturnToken();

        String user1TransactionId = TransactionActions.withToken(user1accessToken)
                .createAndReturnId("INCOME", "10", TEST_DATE);

        String user2accessToken = UserActions.randomUser().signUpAndReturnToken();
        TransactionActions user2TransactionActions = TransactionActions.withToken(user2accessToken);

        user2TransactionActions.delete(user1TransactionId)
                .statusCode(HttpStatus.SC_NOT_FOUND);

        user2TransactionActions.update(user1TransactionId, "20", TEST_DATE)
                .statusCode(HttpStatus.SC_NOT_FOUND);

        user2TransactionActions.getAll()
                .body("size()", is(0));
    }

}
