package com.myprojects.expense.blackbox.test;

import com.myprojects.expense.blackbox.util.TransactionActions;
import com.myprojects.expense.blackbox.util.UserActions;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

public class ApiSecurityTests {

    @Test
    public void testSignUpAndLogin() {
        UserActions userActions = new UserActions(UUID.randomUUID() + "@gmail.com", "Abcd1234");

        String accessToken = userActions.signUp()
                .extract()
                .jsonPath().get("token");

        new TransactionActions(accessToken).getAll();

        String anotherToken = userActions.login()
                .extract()
                .jsonPath().get("token");

        new TransactionActions(anotherToken).getAll();
    }

    @DataProvider
    public Object[][] testUnauthenticatedRequestCases() {
        return new Object[][] {
                {"/v1/transactions"},
                {"/v1/reports/2020/22/2"},
                {"/v1/users"},
        };
    }

    @Test(dataProvider = "testUnauthenticatedRequestCases")
    public void testUnauthenticatedRequest(String path) {
        RestAssured.get(path)
                .then()
                .log().all()
                .statusCode(equalTo(HttpStatus.SC_FORBIDDEN))
                .contentType(ContentType.JSON)
                .body("message", equalTo("Access denied!"));
    }

    @DataProvider
    public Object[][] testNotFoundRequestCases() {
        return new Object[][] {
                {"/v1/whatever"},
                {"/wrong"},
        };
    }

    @Test(dataProvider = "testNotFoundRequestCases")
    public void testNotFoundRequest(String path) {
        RestAssured.get(path)
                .then()
                .log().all()
                .statusCode(equalTo(HttpStatus.SC_NOT_FOUND))
                .contentType(ContentType.JSON)
                .body("message", equalTo("Path not found!"));
    }

}
