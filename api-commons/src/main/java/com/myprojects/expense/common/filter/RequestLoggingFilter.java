package com.myprojects.expense.common.filter;

import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * Logs the http request info before processing it.
 */
public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

    public RequestLoggingFilter(boolean includePayload) {
        setBeforeMessagePrefix("[");
        setAfterMessagePrefix("[");
        setIncludeQueryString(true);
        setIncludePayload(includePayload);
        setMaxPayloadLength(1000);
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return true;
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.info(String.format("START %s", message));
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.info(String.format("END   %s", message));
    }

}