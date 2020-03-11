package com.myprojects.expense.blackbox.util;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserActions {

    private static final String USERS_URL = "http://localhost:8080/v1/users";
    private static final String ACCESS_TOKENS_URL = "http://localhost:8080/v1/access-tokens";
    private static final String DEFAULT_USER_PASS = "Abcd1234";

    private final String email;
    private final String password;

    protected UserActions(String email, String password) {
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

    public String signUpAndReturnToken() {
        return signUp()
                .extract()
                .jsonPath().get("token");
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

    public String loginAndReturnToken() {
        return login()
                .extract()
                .jsonPath().get("token");
    }

    public static UserActions randomUser() {
        return new UserActions(UUID.randomUUID() + "@gmail.com", DEFAULT_USER_PASS);
    }
}
