package com.myprojects.expense.common.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtVerificationException extends AuthenticationException {

    public JwtVerificationException(Throwable cause) {
        super("An exception occurred during token verification!", cause);
    }
}