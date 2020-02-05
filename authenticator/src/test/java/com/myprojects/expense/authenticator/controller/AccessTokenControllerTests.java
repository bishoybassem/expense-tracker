package com.myprojects.expense.authenticator.controller;

import com.google.common.util.concurrent.Runnables;
import com.myprojects.expense.authenticator.model.request.LoginRequest;
import com.myprojects.expense.authenticator.model.response.LoginResponse;
import com.myprojects.expense.authenticator.service.AccessTokenService;
import com.myprojects.expense.authenticator.test.ControllerTestsBaseConfig;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {AccessTokenController.class, ControllerTestsBaseConfig.class})
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class AccessTokenControllerTests extends AbstractTestNGSpringContextTests {

    private static final String VALID_LOGIN_REQUEST_BODY = "{" +
            "\"email\": \"test.user@gmail.com\"," +
            "\"password\": \"Abc123456\"" +
            "}";

    @MockBean
    private AccessTokenService mockAccessTokenService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeMethod
    public void resetMocks() {
        Mockito.reset(mockAccessTokenService);
    }

    @Test
    public void testLogin() throws Exception {
        LoginResponse response = new LoginResponse();
        response.setToken("t1");

        Mockito.when(mockAccessTokenService.login(any()))
                .thenReturn(response);

        mockMvc.perform(post(AccessTokenController.PATH)
                .content(VALID_LOGIN_REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isCreated())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"token\": \"t1\"}", true));

        ArgumentCaptor<LoginRequest> requestCaptor = ArgumentCaptor.forClass(LoginRequest.class);

        Mockito.verify(mockAccessTokenService).login(requestCaptor.capture());

        LoginRequest request = requestCaptor.getValue();
        assertThat(request.getEmail(), is("test.user@gmail.com"));
        assertThat(request.getPassword(), is("Abc123456"));
    }

    @DataProvider
    public Object[][] testErrorResponseCases() {
        return new Object[][]{
                {Runnables.doNothing(),
                        post(AccessTokenController.PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"),
                        HttpStatus.BAD_REQUEST,
                        "{\"message\":\"Bad request!\",\"errors\":[" +
                                "\"'password' must not be empty\"," +
                                "\"'email' must not be empty\"" +
                                "]}",
                        false
                },
                {(Runnable) () -> Mockito.doThrow(new BadCredentialsException("some ex for testing!"))
                        .when(mockAccessTokenService).login(any()),
                        post(AccessTokenController.PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VALID_LOGIN_REQUEST_BODY),
                        HttpStatus.BAD_REQUEST,
                        "{\"message\":\"User authentication failed!\"}",
                        true
                }
        };
    }

    @Test(dataProvider = "testErrorResponseCases")
    public void testErrorResponse(Runnable beforeRequest, MockHttpServletRequestBuilder requestBuilder,
                                  HttpStatus expectedStatus, String expectedJsonResponse,
                                  boolean strictJsonComparison) throws Exception {
        beforeRequest.run();

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status()
                        .is(expectedStatus.value()))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json(expectedJsonResponse, strictJsonComparison));
    }

}