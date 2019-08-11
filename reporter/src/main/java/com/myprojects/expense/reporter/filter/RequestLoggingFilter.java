package com.myprojects.expense.reporter.filter;

import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

    public RequestLoggingFilter() {
        setBeforeMessagePrefix("[");
        setIncludeQueryString(true);
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return true;
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        //Log nothing
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.info(String.format("%s %s",request.getMethod(), message));
    }

}