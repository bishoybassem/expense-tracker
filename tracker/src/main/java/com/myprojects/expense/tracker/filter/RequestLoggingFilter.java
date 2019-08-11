package com.myprojects.expense.tracker.filter;

import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

    public RequestLoggingFilter() {
        setBeforeMessagePrefix("[");
        setAfterMessagePrefix("[");
        setIncludeQueryString(true);
        setIncludePayload(true);
        setMaxPayloadLength(1000);
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return true;
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.info(String.format("START %s %s",request.getMethod(), message));
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.info(String.format("END   %s %s",request.getMethod(), message));
    }

}