package com.myprojects.expense.blackbox.util;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class TransactionActions {

    private static final String TRANSACTIONS_URL = "http://localhost:8080/v1/transactions";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final String accessToken;

    protected TransactionActions(String accessToken) {
        this.accessToken = accessToken;
    }

    public ValidatableResponse create(String type, String amount, LocalDate date) {
        return given()
                .body("{" +
                        "\"type\":\"" + type + "\"," +
                        "\"amount\":\"" + amount + "\"," +
                        "\"date\":\""+ DATE_TIME_FORMATTER.format(date) +"\"," +
                        "\"category\":\"tests\"," +
                        "\"comment\":\"comment\"" +
                        "}")
                .header("X-Access-Token", accessToken)
                .contentType(ContentType.JSON)
                .post(TRANSACTIONS_URL)
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .body("id", not(emptyString()));
    }

    public String createAndReturnId(String type, String amount, LocalDate date) {
        return create(type, amount, date)
                .extract()
                .jsonPath().get("id");
    }

    public ValidatableResponse update(String id, String amount, LocalDate date) {
        return given()
                .body("{" +
                        "\"amount\":\"" + amount + "\"," +
                        "\"date\":\""+ DATE_TIME_FORMATTER.format(date) +"\"," +
                        "\"category\":\"tests\"," +
                        "\"comment\":\"comment\"" +
                        "}")
                .header("X-Access-Token", accessToken)
                .contentType(ContentType.JSON)
                .put(TRANSACTIONS_URL + "/" + id)
                .then()
                .log().all()
                .statusCode(anyOf(equalTo(HttpStatus.SC_OK), equalTo(HttpStatus.SC_NOT_FOUND)))
                .contentType(ContentType.JSON)
                .body("id", not(emptyString()));
    }

    public ValidatableResponse delete(String id) {
        return given()
                .header("X-Access-Token", accessToken)
                .delete(TRANSACTIONS_URL + "/" + id)
                .then()
                .log().all()
                .statusCode(anyOf(equalTo(HttpStatus.SC_NO_CONTENT), equalTo(HttpStatus.SC_NOT_FOUND)));
    }

    public ValidatableResponse getAll() {
        return given()
                .header("X-Access-Token", accessToken)
                .get(TRANSACTIONS_URL)
                .then()
                .log().all()
                .statusCode(equalTo(HttpStatus.SC_OK))
                .contentType(ContentType.JSON);
    }

    public static TransactionActions withToken(String accessToken) {
        return new TransactionActions(accessToken);
    }

}
