package com.myprojects.expense.authenticator.controller;

import com.google.common.util.concurrent.Runnables;
import com.myprojects.expense.authenticator.config.AuthenticatorControllerConfig;
import com.myprojects.expense.authenticator.config.AuthenticatorWebSecurityConfig;
import com.myprojects.expense.authenticator.exception.EmailAlreadyUsedException;
import com.myprojects.expense.authenticator.model.request.SignUpRequest;
import com.myprojects.expense.authenticator.service.AppUserService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {AuthenticatorControllerConfig.class, AuthenticatorWebSecurityConfig.class})
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class AppUserControllerTests extends AbstractTestNGSpringContextTests {

    private static final String VALID_SIGN_UP_REQUEST_BODY = "{" +
            "\"name\": \"test_user\"," +
            "\"email\": \"test.user@gmail.com\"," +
            "\"password\": \"Abc123456\"" +
            "}";

    @MockBean
    private AppUserService mockUserService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeMethod
    public void resetMocks() {
        Mockito.reset(mockUserService);
    }

    @Test
    public void testSignUp() throws Exception {
        Mockito.doNothing()
                .when(mockUserService).signUp(any());

        mockMvc.perform(post(AppUserController.PATH)
                .content(VALID_SIGN_UP_REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNoContent())
                .andExpect(header()
                        .doesNotExist(HttpHeaders.CONTENT_TYPE))
                .andExpect(content()
                        .string(""));

        ArgumentCaptor<SignUpRequest> requestCaptor =
                ArgumentCaptor.forClass(SignUpRequest.class);

        Mockito.verify(mockUserService).signUp(requestCaptor.capture());

        SignUpRequest request = requestCaptor.getValue();
        assertThat(request.getName(), is("test_user"));
        assertThat(request.getEmail(), is("test.user@gmail.com"));
        assertThat(request.getPassword(), is("Abc123456"));
    }

    @DataProvider
    public Object[][] testErrorResponseCases() {
        return new Object[][]{
                {Runnables.doNothing(),
                        post(AppUserController.PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{" +
                                "\"name\": \"" + "X".repeat(256) + "\"," +
                                "\"email\": \"invalid\"," +
                                "\"password\": \"......\"" +
                                "}"),
                        HttpStatus.BAD_REQUEST,
                        "{\"message\":\"Bad request!\",\"errors\":[" +
                                "\"'password' Password must contain at least 1 lowercase characters.\"," +
                                "\"'password' Password must contain at least 1 digit characters.\"," +
                                "\"'name' size must be between 1 and 255\"," +
                                "\"'email' must be a well-formed email address\"," +
                                "\"'password' Password must contain at least 1 uppercase characters.\"," +
                                "\"'password' Password must be at least 8 characters in length.\"" +
                                "]}",
                        false
                },
                {(Runnable) () -> Mockito.doThrow(new EmailAlreadyUsedException())
                        .when(mockUserService).signUp(any()),
                        post(AppUserController.PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VALID_SIGN_UP_REQUEST_BODY),
                        HttpStatus.BAD_REQUEST,
                        "{\"message\":\"A user is already registered with the provided email!\"}",
                        true
                },
                {(Runnable) () -> Mockito.doThrow(new RuntimeException("some exception for testing"))
                        .when(mockUserService).signUp(any()),
                        post(AppUserController.PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VALID_SIGN_UP_REQUEST_BODY),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "{\"message\":\"An internal error has occurred!\"}",
                        true
                },
                {Runnables.doNothing(),
                        get("wrong-path"),
                        HttpStatus.FORBIDDEN,
                        "{\"message\":\"Access denied!\"}",
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