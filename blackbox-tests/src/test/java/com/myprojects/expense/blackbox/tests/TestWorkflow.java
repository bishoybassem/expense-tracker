package com.myprojects.expense.blackbox.tests;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class TestWorkflow {

    private static final String TRANSACTIONS_URL = "http://localhost:8080/tracker/transactions";
    private static final String REPORTS_URL = "http://localhost:8081/reporter/reports";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final LocalDate TEST_DATE = LocalDate.of(2017, 4, 20);

    @Test
    public void testCreateTransactions() throws Exception {
        emptyReport(TEST_DATE);

        createTransaction("INCOME", "10", TEST_DATE);
        createTransaction("EXPENSE", "1.23", TEST_DATE);

        await().until(() -> getReport(TEST_DATE)
                .getString("stats.total"), equalTo("8.77"));
    }

    private static void emptyReport(LocalDate date) {
        JsonPath reportJsonPath = getReport(date);
        List<String> ids = reportJsonPath.getList("incomes.id");
        ids.addAll(reportJsonPath.getList("expenses.id"));

        ids.forEach(id -> delete(TRANSACTIONS_URL + "/" + id)
                .then()
                .statusCode(200));

        await().until(() -> getReport(date)
                .getDouble("stats.total"), equalTo(0.0));
    }

    private static void createTransaction(String type, String amount, LocalDate date) {
        given()
                .body("{" +
                        "\"type\":\"" + type + "\"," +
                        "\"amount\":\"" + amount + "\"," +
                        "\"category\":\"abc\"," +
                        "\"date\":\""+ date.format(DATE_TIME_FORMATTER) +"\"," +
                        "\"comment\":\"comment\"" +
                        "}")
                .contentType(ContentType.JSON)
                .post(TRANSACTIONS_URL)
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", not(isEmptyString()));
    }

    private static JsonPath getReport(LocalDate date) {
        return get(reportUrl(date))
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath();
    }

    private static String reportUrl(LocalDate date) {
        return REPORTS_URL + "/" + date.format(DATE_TIME_FORMATTER);
    }

}
