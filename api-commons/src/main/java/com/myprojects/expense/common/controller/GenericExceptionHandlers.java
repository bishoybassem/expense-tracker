package com.myprojects.expense.common.controller;

import com.myprojects.expense.common.model.response.ErrorResponse;
import com.myprojects.expense.common.model.response.ValidationErrorResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GenericExceptionHandlers {

    private static final Log LOGGER = LogFactory.getLog(GenericExceptionHandlers.class);

    /**
     * Translates a {@link MethodArgumentNotValidException} into a {@link ValidationErrorResponse}, populated
     * with all the validation issues that were encountered.
     */
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

    /**
     * Translates a {@link MethodArgumentTypeMismatchException} into an {@link ErrorResponse}, stating in its message
     * which argument has an incorrect type.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentTypeMismatchException exception) {
        return new ErrorResponse("The provided value '%s' is an invalid %s!",
                exception.getValue(), exception.getRequiredType().getSimpleName());
    }

    /**
     * Translates a {@link HttpMediaTypeNotSupportedException} into an {@link ErrorResponse}, stating in its message
     * that the media type used is not supported.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        ErrorResponse response = new ErrorResponse("Media type '" + exception.getContentType() + "' is not supported!");
        return response;
    }

    /**
     * A generic handler for exceptions, invoked  in case none of the other handlers could handle the exception.
     * It returns a generic {@link ErrorResponse} with server error status, and logs the exception.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleGenericException(Exception ex) {
        LOGGER.error("An exception occurred while processing request!", ex);
        return new ErrorResponse("An internal error has occurred!");
    }

}
