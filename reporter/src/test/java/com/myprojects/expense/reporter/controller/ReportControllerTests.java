package com.myprojects.expense.reporter.controller;

import com.myprojects.expense.reporter.config.ReporterControllerConfig;
import com.myprojects.expense.reporter.exception.ReportNotFoundException;
import com.myprojects.expense.reporter.model.ReportStats;
import com.myprojects.expense.reporter.model.ReportTransaction;
import com.myprojects.expense.reporter.model.response.DayReportResponse;
import com.myprojects.expense.reporter.service.ReportService;
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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@ContextConfiguration(classes = ReporterControllerConfig.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class ReportControllerTests extends AbstractTestNGSpringContextTests {

    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final String TEST_DATE_PATH = TEST_DATE.format(DateTimeFormatter.ofPattern("/yyyy/MM/dd"));
    private static final String TEST_DATE_RESPONSE = TEST_DATE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    @MockBean
    private ReportService mockReportService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeMethod
    public void resetMocks() {
        Mockito.reset(mockReportService);
    }

    @Test
    public void testGetRequest() throws Exception {
        Mockito.when(mockReportService.getDayReport(eq(TEST_DATE.getYear()), eq(TEST_DATE.getMonthValue()),
                eq(TEST_DATE.getDayOfMonth())))
                .thenReturn(createReport());

        mockMvc.perform(get(ReportController.PATH + TEST_DATE_PATH))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\n" +
                                "  \"date\": \"" + TEST_DATE_RESPONSE + "\",\n" +
                                "  \"stats\": {\n" +
                                "    \"total\": 10,\n" +
                                "    \"totalIncomes\": 10,\n" +
                                "    \"totalExpenses\": 0\n" +
                                "  },\n" +
                                "  \"incomes\": [\n" +
                                "    {\n" +
                                "      \"amount\": 10,\n" +
                                "      \"category\": \"test\",\n" +
                                "      \"id\": \"tid\"\n" +
                                "    }\n" +
                                "  ],\n" +
                                "  \"expenses\": []\n" +
                                "}", true));
    }

    @Test
    public void testGetRequestNotFound() throws Exception {
        Mockito.when(mockReportService.getDayReport(eq(TEST_DATE.getYear()), eq(TEST_DATE.getMonthValue()),
                eq(TEST_DATE.getDayOfMonth())))
                .thenThrow(new ReportNotFoundException(TEST_DATE));

        mockMvc.perform(get(ReportController.PATH + TEST_DATE_PATH))
                .andDo(print())
                .andExpect(status()
                        .isNotFound())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"message\":\"Report for date (" + TEST_DATE_RESPONSE
                                + ") is not found!\"}", true));
    }

    @DataProvider
    public static Object[][] testGetRequestValidationCases() {
        return new Object[][] {
                {"/aaaa/bb/cc", "{\"message\":\"An invalid date has been requested!\"}"},
                {"/2020/bb/cc", "{\"message\":\"An invalid date has been requested!\"}"},
                {"/2020/12/cc", "{\"message\":\"An invalid date has been requested!\"}"},
                {"/-1/13/50", "{\"message\":\"An invalid date has been requested!\"}"},
        };
    }

    @Test(dataProvider = "testGetRequestValidationCases")
    public void testGetRequestValidation(String path, String expectedError) throws Exception {
        Mockito.doCallRealMethod()
                .when(mockReportService).getDayReport(anyInt(), anyInt(), anyInt());

        mockMvc.perform(get(ReportController.PATH + path))
                .andDo(print())
                .andExpect(status()
                        .isBadRequest())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json(expectedError, true));
    }

    @Test
    public void testGenericExceptionResponse() throws Exception {
        Mockito.doThrow(new RuntimeException("some exception for testing"))
                .when(mockReportService).getDayReport(anyInt(), anyInt(), anyInt());

        mockMvc.perform(get(ReportController.PATH + TEST_DATE_PATH))
                .andDo(print())
                .andExpect(status()
                        .isInternalServerError())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"message\":\"An internal error has occurred!\"}", true));
    }

    private static DayReportResponse createReport() {
        DayReportResponse report = new DayReportResponse();
        report.setDate(TEST_DATE);
        report.setStats(new ReportStats(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO));
        report.setIncomes(Arrays.asList(new ReportTransaction("tid", BigDecimal.TEN, "test")));
        report.setExpenses(emptyList());
        return report;
    }
}