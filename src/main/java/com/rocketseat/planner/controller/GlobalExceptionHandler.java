package com.rocketseat.planner.controller;

import com.rocketseat.planner.exceptions.DataNotFoundException;
import com.rocketseat.planner.exceptions.RequiredArgumentsIllegalException;
import com.rocketseat.planner.models.ErrorResponse;
import com.rocketseat.planner.validations.ValidationErrorResponse;
import com.rocketseat.planner.validations.ValidationErrorResponse.FieldError;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RequiredArgumentsIllegalException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleIllegalArgsException(Exception exception) {
    ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.BAD_REQUEST.toString())
        .message("Error in client args").errorDescription(exception.getMessage()).build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorResponse> handleDataNotFoundException(Exception exception, HttpServletRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.NOT_FOUND.toString())
        .message("Data not found").errorDescription(exception.getMessage()).build();

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleAllException(Exception exception) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.toString()).message("The server could not process")
        .errorDescription(exception.getMessage()).build();
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> new ValidationErrorResponse.FieldError(
            error.getField(),
            error.getRejectedValue() != null ? error.getRejectedValue().toString() : null,
            error.getDefaultMessage()
        ))
        .collect(Collectors.toList());

    ValidationErrorResponse errorResponse = new ValidationErrorResponse(
        HttpStatus.BAD_REQUEST.toString(),
        "Validation failed",
        fieldErrors
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

}
