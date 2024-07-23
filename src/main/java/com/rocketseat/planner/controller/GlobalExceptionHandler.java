package com.rocketseat.planner.controller;

import com.rocketseat.planner.exceptions.DataNotFoundException;
import com.rocketseat.planner.models.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllException(Exception exception) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.toString()).message("The server could not process")
        .errorDescription(exception.getMessage()).build();
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(DataNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleDataNotFoundException(Exception exception) {
    ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.NOT_FOUND.toString())
        .message("Data not found").errorDescription(exception.getMessage()).build();
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgsException(Exception exception) {
    ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.BAD_REQUEST.toString())
        .message("Error in client args").errorDescription(exception.getMessage()).build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

}
