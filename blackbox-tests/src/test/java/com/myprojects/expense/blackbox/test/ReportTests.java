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
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.comparesEqualTo;

public class ReportTests {

    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final String TEST_USER_EMAIL = UUID.randomUUID() + "@gmail.com";
    private static final String TEST_USER_PASS = "Abcd1234";

    @Test
    public void testReportWithDifferentTransactionActions() {
        String accessToken = new UserActions(TEST_USER_EMAIL, TEST_USER_PASS).signUp()
                .extract()
                .jsonPath().get("token");

        ReportActions reportActions = new ReportActions(accessToken);
        TransactionActions transactionActions = new TransactionActions(accessToken);

        emptyReport(TEST_DATE, reportActions, transactionActions);

        String incomeId = transactionActions.create("INCOME", "10", TEST_DATE)
                .extract().jsonPath().get("id");
        String expenseId = transactionActions.create("EXPENSE", "1.23", TEST_DATE)
                .extract().jsonPath().get("id");

        awaitReport(TEST_DATE, "8.77", reportActions);

        transactionActions.update(incomeId, "20", TEST_DATE);
        transactionActions.delete(expenseId);

        awaitReport(TEST_DATE, "20", reportActions);
    }

    private static void emptyReport(LocalDate date, ReportActions reportActions,
                                   TransactionActions transactionActions) {
        ExtractableResponse<Response> response = reportActions.get(date).extract();
        if (response.statusCode() == HttpStatus.SC_NOT_FOUND) {
            return;
        }

        Arrays.asList("incomes.id", "expenses.id").forEach(path -> {
            response.jsonPath().getList(path, String.class)
                    .forEach(transactionActions::delete);
        });

        awaitReport(date, "0", reportActions);
    }

    private static void awaitReport(LocalDate date, String expectedAmount, ReportActions reportActions) {
        await().until(() -> reportActions.get(date).extract()
                .jsonPath()
                .get("stats.total"), comparesEqualTo(new BigDecimal(expectedAmount)));
    }
}
