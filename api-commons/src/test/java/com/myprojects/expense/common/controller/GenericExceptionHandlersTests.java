package com.myprojects.expense.common.controller;

import com.myprojects.expense.common.test.ControllerTestsConfig;
import com.myprojects.expense.common.test.EchoController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = ControllerTestsConfig.class)
public class GenericExceptionHandlersTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    @DataProvider
    public Object[][] testErrorResponseCases() {
        return new Object[][] {
                {get(EchoController.ECHO_PATH + "/wrong-id"),
                        HttpStatus.BAD_REQUEST,
                        "{\"message\":\"The provided value 'wrong-id' is an invalid UUID!\"}",
                        true
                },
                {post(EchoController.ECHO_PATH)
                        .content("{\"param1\": \"x\"}")
                        .contentType(MediaType.APPLICATION_JSON),
                        HttpStatus.BAD_REQUEST,
                        "{\"message\":\"Bad request!\",\"errors\":[" +
                                "\"'param2' must not be empty\"," +
                                "\"'param1' size must be between 10 and 20\"" +
                                "]}",
                        false
                },
                {post(EchoController.ECHO_PATH)
                        .content("param1=x")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED),
                        HttpStatus.BAD_REQUEST,
                        "{\"message\":\"Media type 'application/x-www-form-urlencoded' is not supported!\"}",
                        false
                },
                {get(EchoController.THROW_EX_PATH),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "{\"message\":\"An internal error has occurred!\"}",
                        true
                },
        };
    }

    @Test(dataProvider = "testErrorResponseCases")
    public void testErrorResponse(MockHttpServletRequestBuilder requestBuilder,
                                  HttpStatus expectedStatus, String expectedJsonResponse,
                                  boolean strictJsonComparison) throws Exception {
        mockMvc.perform(requestBuilder
                .with(authentication(createAuthenticatedToken())))
                .andDo(print())
                .andExpect(status()
                        .is(expectedStatus.value()))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json(expectedJsonResponse, strictJsonComparison));
    }

    private static Authentication createAuthenticatedToken() {
        return new UsernamePasswordAuthenticationToken("some-id", null,
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }

}