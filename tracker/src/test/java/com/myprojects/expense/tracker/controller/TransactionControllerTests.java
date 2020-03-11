package com.myprojects.expense.tracker.controller;

import com.google.common.util.concurrent.Runnables;
import com.myprojects.expense.tracker.config.TrackerControllerConfig;
import com.myprojects.expense.tracker.config.TrackerWebSecurityConfig;
import com.myprojects.expense.tracker.exception.TransactionNotFoundException;
import com.myprojects.expense.tracker.model.TransactionType;
import com.myprojects.expense.tracker.model.request.CreateTransactionRequest;
import com.myprojects.expense.tracker.model.request.UpdateTransactionRequest;
import com.myprojects.expense.tracker.model.response.TransactionResponse;
import com.myprojects.expense.tracker.service.TransactionService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@ContextConfiguration(classes = {TrackerControllerConfig.class, TrackerWebSecurityConfig.class})
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class TransactionControllerTests extends AbstractTestNGSpringContextTests {

    @MockBean
    private TransactionService mockTransactionService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeMethod
    public void resetMocks() {
        Mockito.reset(mockTransactionService);
    }

    @Test
    public void testCreateRequest() throws Exception {
        final UUID transactionId = UUID.randomUUID();
        Mockito.when(mockTransactionService.create(any()))
                .thenReturn(createTestTransactionResponse(transactionId));

        mockMvc.perform(post(TransactionController.PATH)
                .content("{"
                        + "\"type\": \"EXPENSE\","
                        + "\"amount\": \"1.23\","
                        + "\"category\": \"abc\","
                        + "\"date\": \"2017/03/20\","
                        + "\"comment\": \"comment\""
                        + "}")
                .contentType(MediaType.APPLICATION_JSON)
                .with(authentication(createAuthenticatedToken())))
                .andDo(print())
                .andExpect(status()
                        .isCreated())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json(expectedJsonResponseBody(transactionId)));

        ArgumentCaptor<CreateTransactionRequest> requestCaptor =
                ArgumentCaptor.forClass(CreateTransactionRequest.class);

        Mockito.verify(mockTransactionService)
                .create(requestCaptor.capture());

        CreateTransactionRequest request = requestCaptor.getValue();
        assertThat(request.getType(), is(TransactionType.EXPENSE.toString()));
        assertThat(request.getAmount(), is(new BigDecimal("1.23")));
        assertThat(request.getCategory(), is("abc"));
        assertThat(request.getDate(), is(LocalDate.of(2017, Month.MARCH, 20)));
        assertThat(request.getComment(), is("comment"));
    }

    @Test
    public void testUpdateRequest() throws Exception {
        final UUID transactionId = UUID.randomUUID();
        Mockito.when(mockTransactionService.update(any(), any()))
                .thenReturn(createTestTransactionResponse(transactionId));

        mockMvc.perform(put(TransactionController.PATH + "/" + transactionId)
                .content("{"
                        + "\"amount\": \"1.23\","
                        + "\"category\": \"abc\","
                        + "\"date\": \"2017/03/20\","
                        + "\"comment\": \"comment\""
                        + "}")
                .contentType(MediaType.APPLICATION_JSON)
                .with(authentication(createAuthenticatedToken())))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json(expectedJsonResponseBody(transactionId)));

        ArgumentCaptor<UpdateTransactionRequest> requestCaptor =
                ArgumentCaptor.forClass(UpdateTransactionRequest.class);

        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);

        Mockito.verify(mockTransactionService)
                .update(uuidCaptor.capture(), requestCaptor.capture());

        assertThat(uuidCaptor.getValue(), is(transactionId));

        UpdateTransactionRequest request = requestCaptor.getValue();
        assertThat(request.getAmount(), is(new BigDecimal("1.23")));
        assertThat(request.getCategory(), is("abc"));
        assertThat(request.getDate(), is(LocalDate.of(2017, Month.MARCH, 20)));
        assertThat(request.getComment(), is("comment"));
    }

    @Test
    public void testGetRequest() throws Exception {
        final UUID transactionId = UUID.randomUUID();
        Mockito.when(mockTransactionService.get(transactionId))
                .thenReturn(createTestTransactionResponse(transactionId));

        mockMvc.perform(get(TransactionController.PATH + "/" + transactionId)
                .with(authentication(createAuthenticatedToken())))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json(expectedJsonResponseBody(transactionId), true));
    }

    @Test
    public void testGetAllRequest() throws Exception {
        final UUID transactionId1 = UUID.randomUUID();
        final UUID transactionId2 = UUID.randomUUID();
        Mockito.when(mockTransactionService.getAll())
                .thenReturn(Arrays.asList(createTestTransactionResponse(transactionId1),
                        createTestTransactionResponse(transactionId2)));

        String expectedResponseBody = "[" + expectedJsonResponseBody(transactionId1) + ","
                + expectedJsonResponseBody(transactionId2) + "]";

        mockMvc.perform(get(TransactionController.PATH)
                .with(authentication(createAuthenticatedToken())))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json(expectedResponseBody, true));
    }

    @Test
    public void testDeleteRequest() throws Exception {
        final UUID transactionId = UUID.randomUUID();
        Mockito.doNothing()
                .when(mockTransactionService).delete(transactionId);

        mockMvc.perform(delete(TransactionController.PATH + "/" + transactionId)
                .with(authentication(createAuthenticatedToken())))
                .andDo(print())
                .andExpect(status()
                        .isNoContent())
                .andExpect(header()
                        .doesNotExist(HttpHeaders.CONTENT_TYPE))
                .andExpect(content()
                        .string(""));
    }

    @DataProvider
    public Object[][] testErrorResponseCases() {
        final UUID transactionId = UUID.randomUUID();
        return new Object[][] {
                {Runnables.doNothing(),
                        post(TransactionController.PATH)
                                .content("{\"type\":\"wrong\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(authentication(createAuthenticatedToken())),
                        HttpStatus.BAD_REQUEST,
                        "{\"message\":\"Bad request!\",\"errors\":[" +
                                "\"'type' has to be either 'INCOME' or 'EXPENSE'\"," +
                                "\"'date' must not be null\"," +
                                "\"'amount' must not be null\"" +
                                "]}",
                        false
                },
                {Runnables.doNothing(),
                        put(TransactionController.PATH + "/" + UUID.randomUUID())
                                .content("{\"category\":\"" + "X".repeat(40) + "\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(authentication(createAuthenticatedToken())),
                        HttpStatus.BAD_REQUEST,
                        "{\"message\":\"Bad request!\",\"errors\":[" +
                                "\"'category' size must be between 0 and 30\"," +
                                "\"'date' must not be null\"," +
                                "\"'amount' must not be null\"" +
                                "]}",
                        false
                },
                {Runnables.doNothing(),
                        put(TransactionController.PATH + "/wrong-id")
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(authentication(createAuthenticatedToken())),
                        HttpStatus.BAD_REQUEST,
                        "{\"message\":\"The provided value 'wrong-id' is an invalid UUID!\"}",
                        true
                },
                {(Runnable) () -> Mockito.doThrow(new TransactionNotFoundException(transactionId))
                        .when(mockTransactionService).delete(any()),
                        delete(TransactionController.PATH + "/" + transactionId)
                                .with(authentication(createAuthenticatedToken())),
                        HttpStatus.NOT_FOUND,
                        "{\"message\":\"Transaction with id (" + transactionId + ") is not found!\"}",
                        true
                },
                {(Runnable) () -> Mockito.doThrow(new RuntimeException("some exception for testing"))
                        .when(mockTransactionService).get(any()),
                        get(TransactionController.PATH + "/" + UUID.randomUUID())
                                .with(authentication(createAuthenticatedToken())),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "{\"message\":\"An internal error has occurred!\"}",
                        true
                },
                {Runnables.doNothing(),
                        get(TransactionController.PATH + "/" + UUID.randomUUID()),
                        HttpStatus.FORBIDDEN,
                        "{\"message\":\"Access denied!\"}",
                        true
                },
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

    private static TransactionResponse createTestTransactionResponse(UUID transactionId) {
        final TransactionResponse response = new TransactionResponse();
        response.setId(transactionId);
        response.setType(TransactionType.EXPENSE);
        response.setAmount(new BigDecimal("1.23"));
        response.setCategory("abc");
        response.setDate(LocalDate.of(2018, Month.FEBRUARY, 13));
        response.setComment("comment");
        return response;
    }

    private static String expectedJsonResponseBody(UUID transactionId) {
        return "{"
                + "\"id\":\"" + transactionId + "\","
                + "\"type\":\"EXPENSE\","
                + "\"amount\":1.23,"
                + "\"category\":\"abc\","
                + "\"date\":\"2018/02/13\","
                + "\"comment\":\"comment\""
                + "}";
    }

    private static Authentication createAuthenticatedToken() {
        return new UsernamePasswordAuthenticationToken(UUID.randomUUID(), null,
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}