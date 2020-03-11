package com.myprojects.expense.blackbox.test;

import com.myprojects.expense.blackbox.util.ReportActions;
import com.myprojects.expense.blackbox.util.TransactionActions;
import com.myprojects.expense.blackbox.util.UserActions;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.comparesEqualTo;

public class ReportTests {

    private static final LocalDate TEST_DATE = LocalDate.now();

    @Test
    public void testReportWithDifferentTransactionActions() {
        String accessToken = UserActions.randomUser().signUpAndReturnToken();
        TransactionActions transactionActions = TransactionActions.withToken(accessToken);

        String incomeId = transactionActions.createAndReturnId("INCOME", "10", TEST_DATE);
        String expenseId = transactionActions.createAndReturnId("EXPENSE", "1.23", TEST_DATE);
        awaitReport(TEST_DATE, "8.77", accessToken);

        transactionActions.update(incomeId, "20", TEST_DATE);
        awaitReport(TEST_DATE, "18.77", accessToken);

        transactionActions.update(expenseId, "1", TEST_DATE);
        awaitReport(TEST_DATE, "19.00", accessToken);

        emptyReport(TEST_DATE, accessToken);
    }

    @Test
    public void testReportsOfDifferentUsers() {
        String user1accessToken = UserActions.randomUser().signUpAndReturnToken();
        String user2accessToken = UserActions.randomUser().signUpAndReturnToken();

        TransactionActions.withToken(user1accessToken)
                .create("INCOME", "10", TEST_DATE);

        TransactionActions.withToken(user2accessToken)
                .create("EXPENSE", "10", TEST_DATE);

        awaitReport(TEST_DATE, "-10", user2accessToken);
        awaitReport(TEST_DATE, "10", user1accessToken);
    }

    private static void emptyReport(LocalDate date, String accessToken) {
        ReportActions reportActions = ReportActions.withToken(accessToken);
        ExtractableResponse<Response> response = reportActions.get(date).extract();
        if (response.statusCode() == HttpStatus.SC_NOT_FOUND) {
            return;
        }

        TransactionActions transactionActions = TransactionActions.withToken(accessToken);
        Arrays.asList("incomes.id", "expenses.id").forEach(path -> {
            response.jsonPath().getList(path, String.class)
                    .forEach(transactionActions::delete);
        });

        awaitReport(date, "0", accessToken);
    }

    private static void awaitReport(LocalDate date, String expectedAmount, String accessToken) {
        ReportActions reportActions = ReportActions.withToken(accessToken);
        await().until(() -> reportActions.get(date).extract()
                .jsonPath()
                .get("stats.total"), comparesEqualTo(new BigDecimal(expectedAmount)));
    }
}
