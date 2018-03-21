package com.myprojects.expense.tracker.controller;

import com.myprojects.expense.tracker.config.TrackerControllerConfig;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(TransactionController.class)
@ContextConfiguration(classes = TrackerControllerConfig.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class TransactionControllerTests extends AbstractTestNGSpringContextTests {

    @MockBean
    private TransactionService mockTransactionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateRequestDeserializesFromJsonCorrectly() throws Exception {
        final String requestBody = "{"
                + "\"type\": \"EXPENSE\","
                + "\"amount\": \"1.23\","
                + "\"category\": \"abc\","
                + "\"date\": \"2017/03/20\","
                + "\"comment\": \"comment\""
                + "}";

        mockMvc.perform(post(TransactionController.PATH)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print());

        ArgumentCaptor<CreateTransactionRequest> requestCaptor =
                ArgumentCaptor.forClass(CreateTransactionRequest.class);

        Mockito.verify(mockTransactionService).create(requestCaptor.capture());

        CreateTransactionRequest request = requestCaptor.getValue();
        assertThat(request.getType(), is(TransactionType.EXPENSE));
        assertThat(request.getAmount(), is(new BigDecimal("1.23")));
        assertThat(request.getCategory(), is("abc"));
        assertThat(request.getDate(), is(LocalDate.of(2017, Month.MARCH, 20)));
        assertThat(request.getComment(), is("comment"));
    }

    @Test
    public void testUpdateRequestDeserializesFromJsonCorrectly() throws Exception {
        final UUID transactionId = UUID.randomUUID();
        final String requestBody = "{"
                + "\"amount\": \"1.23\","
                + "\"category\": \"abc\","
                + "\"date\": \"2017/03/20\","
                + "\"comment\": \"comment\""
                + "}";

        mockMvc.perform(put(TransactionController.PATH + "/" + transactionId)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print());

        ArgumentCaptor<UpdateTransactionRequest> requestCaptor =
                ArgumentCaptor.forClass(UpdateTransactionRequest.class);

        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);

        Mockito.verify(mockTransactionService).update(uuidCaptor.capture(), requestCaptor.capture());

        assertThat(uuidCaptor.getValue(), is(transactionId));

        UpdateTransactionRequest request = requestCaptor.getValue();
        assertThat(request.getAmount(), is(new BigDecimal("1.23")));
        assertThat(request.getCategory(), is("abc"));
        assertThat(request.getDate(), is(LocalDate.of(2017, Month.MARCH, 20)));
        assertThat(request.getComment(), is("comment"));
    }

    @Test
    public void testGetRequestSerializesToJsonCorrectly() throws Exception {
        final UUID transactionId = UUID.randomUUID();
        TransactionResponse mockServiceResponse = createTestTransactionResponse(transactionId);
        Mockito.when(mockTransactionService.get(transactionId)).thenReturn(mockServiceResponse);

        mockMvc.perform(get(TransactionController.PATH + "/" + transactionId))
                .andDo(print())
                .andExpect(content()
                        .json(expectedJsonResponseBody(transactionId), true));
    }

    @Test
    public void testGetAllRequestSerializesToJsonCorrectly() throws Exception {
        final UUID transactionId1 = UUID.randomUUID();
        final UUID transactionId2 = UUID.randomUUID();
        List<TransactionResponse> mockServiceResponse = Arrays.asList(createTestTransactionResponse(transactionId1),
                        createTestTransactionResponse(transactionId2));
        Mockito.when(mockTransactionService.getAll()).thenReturn(mockServiceResponse);

        String expectedResponseBody = "[" + expectedJsonResponseBody(transactionId1) + ","
                + expectedJsonResponseBody(transactionId2) + "]";
        mockMvc.perform(get(TransactionController.PATH))
                .andDo(print())
                .andExpect(content()
                        .json(expectedResponseBody, true));
    }

    @Test
    public void testDeleteRequestSerializesToJsonCorrectly() throws Exception {
        final UUID transactionId = UUID.randomUUID();
        TransactionResponse mockServiceResponse = createTestTransactionResponse(transactionId);
        Mockito.when(mockTransactionService.delete(transactionId)).thenReturn(mockServiceResponse);

        mockMvc.perform(delete(TransactionController.PATH + "/" + transactionId))
                .andDo(print())
                .andExpect(content()
                        .json(expectedJsonResponseBody(transactionId), true));
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
}