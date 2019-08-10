package com.myprojects.expense.blackbox.tests;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;

public class TestWorkflow {

    private static final String TRANSACTIONS_URL = "http://localhost:8080/v1/transactions";
    private static final String REPORTS_URL = "http://localhost:8080/v1/reports";
    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Test
    public void testCreateTransactions() {
        emptyReport(TEST_DATE);

        createTransaction("INCOME", "10", TEST_DATE);
        createTransaction("EXPENSE", "1.23", TEST_DATE);

        await().until(() -> getReport(TEST_DATE)
                .jsonPath()
                .getString("stats.total"), equalTo("8.77"));
    }

    private static void emptyReport(LocalDate date) {
        ExtractableResponse<Response> response = getReport(date);
        if (response.statusCode() == 404) {
            return;
        }

        List<String> allIds = new ArrayList<>();
        Arrays.asList("incomes.id", "expenses.id").forEach(path -> {
            List<String> ids = response.jsonPath().getList(path);
            if (ids != null) {
                allIds.addAll(ids);
            }
        });

        allIds.forEach(id -> delete(TRANSACTIONS_URL + "/" + id)
                .then()
                .log().all()
                .statusCode(200));

        await().until(() -> getReport(date)
                .jsonPath()
                .getDouble("stats.total"), equalTo(0.0));
    }

    private static void createTransaction(String type, String amount, LocalDate date) {
        given()
                .body("{" +
                        "\"type\":\"" + type + "\"," +
                        "\"amount\":\"" + amount + "\"," +
                        "\"category\":\"abc\"," +
                        "\"date\":\""+ DATE_TIME_FORMATTER.format(date) +"\"," +
                        "\"comment\":\"comment\"" +
                        "}")
                .contentType(ContentType.JSON)
                .post(TRANSACTIONS_URL)
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", not(emptyString()));
    }

    private static ExtractableResponse<Response> getReport(LocalDate date) {
        return get(reportUrl(date))
                .then()
                .log().all()
                .statusCode(anyOf(equalTo(200), equalTo(404)))
                .contentType(ContentType.JSON)
                .extract();
    }

    private static String reportUrl(LocalDate date) {
        return REPORTS_URL + "/" + DATE_TIME_FORMATTER.format(date);
    }

}
