package com.myprojects.expense.reporter.controller;

import com.google.common.util.concurrent.Runnables;
import com.myprojects.expense.reporter.config.ReporterControllerConfig;
import com.myprojects.expense.reporter.config.ReporterWebSecurityConfig;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@ContextConfiguration(classes = {ReporterControllerConfig.class, ReporterWebSecurityConfig.class})
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

        mockMvc.perform(get(ReportController.PATH + TEST_DATE_PATH)
                .with(authentication(createAuthenticatedToken())))
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

    @DataProvider
    public Object[][] testErrorResponseCases() {
        return new Object[][] {
                {(Runnable) () ->  Mockito.when(mockReportService.getDayReport(anyInt(), anyInt(), anyInt()))
                        .thenThrow(new ReportNotFoundException(TEST_DATE)),
                        get(ReportController.PATH + TEST_DATE_PATH)
                                .with(authentication(createAuthenticatedToken())),
                        HttpStatus.NOT_FOUND,
                        "{\"message\":\"Report for date (" + TEST_DATE_RESPONSE + ") is not found!\"}",
                        true
                },
                {(Runnable) () -> Mockito.doCallRealMethod()
                        .when(mockReportService).getDayReport(anyInt(), anyInt(), anyInt()),
                        get(ReportController.PATH + "/aaaa/bb/cc")
                                .with(authentication(createAuthenticatedToken())),
                        HttpStatus.BAD_REQUEST,
                        "{\"message\":\"An invalid date has been requested!\"}",
                        true
                },
                {(Runnable) () -> Mockito.doCallRealMethod()
                        .when(mockReportService).getDayReport(anyInt(), anyInt(), anyInt()),
                        get(ReportController.PATH + "/-1/13/50")
                                .with(authentication(createAuthenticatedToken())),
                        HttpStatus.BAD_REQUEST,
                        "{\"message\":\"An invalid date has been requested!\"}",
                        true
                },
                {(Runnable) () -> Mockito.doThrow(new RuntimeException("some exception for testing"))
                        .when(mockReportService).getDayReport(anyInt(), anyInt(), anyInt()),
                        get(ReportController.PATH + TEST_DATE_PATH)
                                .with(authentication(createAuthenticatedToken())),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "{\"message\":\"An internal error has occurred!\"}",
                        true
                },
                {Runnables.doNothing(),
                        get(ReportController.PATH + TEST_DATE_PATH),
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

    private static DayReportResponse createReport() {
        DayReportResponse report = new DayReportResponse();
        report.setDate(TEST_DATE);
        report.setStats(new ReportStats(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO));
        report.setIncomes(Arrays.asList(new ReportTransaction("tid", BigDecimal.TEN, "test")));
        report.setExpenses(emptyList());
        return report;
    }

    private static Authentication createAuthenticatedToken() {
        return new UsernamePasswordAuthenticationToken(UUID.randomUUID(), null,
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}