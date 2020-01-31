package com.myprojects.expense.tracker.controller;

import com.myprojects.expense.tracker.exception.TransactionNotFoundException;
import com.myprojects.expense.tracker.model.response.ErrorResponse;
import com.myprojects.expense.tracker.model.response.ValidationErrorResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class TrackerExceptionHandlers {

    private static final Log LOGGER = LogFactory.getLog(TrackerExceptionHandlers.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleTransactionNotFoundException(TransactionNotFoundException exception) {
        return new ErrorResponse("Transaction with id (" + exception.getTransactionId() + ") is not found!");
    }

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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentTypeMismatchException exception) {
        ErrorResponse response = new ErrorResponse("The provided value '%s' is an invalid %s",
                exception.getValue(), exception.getRequiredType().getSimpleName());
        return response;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleGenericException(Exception ex) {
        LOGGER.error("An exception occurred while processing request!", ex);
        return new ErrorResponse("An internal error has occurred!");
    }

}
