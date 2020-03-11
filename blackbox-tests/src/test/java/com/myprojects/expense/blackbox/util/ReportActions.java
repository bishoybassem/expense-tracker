package com.myprojects.expense.blackbox.util;

import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig.NumberReturnType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

public class ReportActions {

    private static final String REPORTS_URL = "http://localhost:8080/v1/reports";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final RestAssuredConfig REST_ASSURED_CONFIG = new RestAssuredConfig()
            .jsonConfig(jsonConfig().numberReturnType(NumberReturnType.BIG_DECIMAL));

    private final String accessToken;

    protected ReportActions(String accessToken) {
        this.accessToken = accessToken;
    }

    public ValidatableResponse get(LocalDate date) {
        return given()
                .config(REST_ASSURED_CONFIG)
                .header("X-Access-Token", accessToken)
                .get(REPORTS_URL + "/" + DATE_TIME_FORMATTER.format(date))
                .then()
                .log().all()
                .statusCode(anyOf(equalTo(HttpStatus.SC_OK), equalTo(HttpStatus.SC_NOT_FOUND)))
                .contentType(ContentType.JSON);
    }

    public static ReportActions withToken(String accessToken) {
        return new ReportActions(accessToken);
    }

}