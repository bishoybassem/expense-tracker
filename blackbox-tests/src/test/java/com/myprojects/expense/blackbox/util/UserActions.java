package com.myprojects.expense.blackbox.util;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserActions {

    private static final String USERS_URL = "http://localhost:8080/v1/users";
    private static final String ACCESS_TOKENS_URL = "http://localhost:8080/v1/access-tokens";

    private final String email;
    private final String password;

    public UserActions(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public ValidatableResponse signUp() {
        return given()
                .body("{" +
                        "\"email\":\"" + email + "\"," +
                        "\"name\":\"" + email + "\"," +
                        "\"password\":\"" + password + "\"" +
                        "}")
                .contentType(ContentType.JSON)
                .post(USERS_URL)
                .then()
                .log().all()
                .statusCode(equalTo(HttpStatus.SC_CREATED))
                .contentType(ContentType.JSON);
    }

    public ValidatableResponse login() {
        return given()
                .body("{" +
                        "\"email\":\"" + email + "\"," +
                        "\"password\":\"" + password + "\"" +
                        "}")
                .contentType(ContentType.JSON)
                .post(ACCESS_TOKENS_URL)
                .then()
                .log().all()
                .statusCode(equalTo(HttpStatus.SC_CREATED))
                .contentType(ContentType.JSON);
    }

}
