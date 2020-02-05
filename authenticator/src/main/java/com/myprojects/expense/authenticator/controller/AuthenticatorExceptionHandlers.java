package com.myprojects.expense.authenticator.controller;

import com.myprojects.expense.authenticator.exception.EmailAlreadyUsedException;
import com.myprojects.expense.authenticator.model.response.ErrorResponse;
import com.myprojects.expense.authenticator.model.response.ValidationErrorResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AuthenticatorExceptionHandlers {

    private static final Log LOGGER = LogFactory.getLog(AuthenticatorExceptionHandlers.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ValidationErrorResponse response = new ValidationErrorResponse("Bad request!");
        exception.getBindingResult().getAllErrors().stream()
                .filter(error -> error instanceof FieldError)
                .forEach(error -> {
                    FieldError fieldError = (FieldError) error;
                    response.addError(String.format("'%s' %s", fieldError.getField(), fieldError.getDefaultMessage()));
                });
        return response;
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleEmailAlreadyUsedException() {
        return new ErrorResponse("A user is already registered with the provided email!");
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleAuthenticationException() {
        return new ErrorResponse("User authentication failed!");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleGenericException(Exception ex) {
        LOGGER.error("An exception occurred while processing request!", ex);
        return new ErrorResponse("An internal error has occurred!");
    }

}
