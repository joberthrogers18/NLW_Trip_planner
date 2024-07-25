package com.rocketseat.planner.validations;

import java.util.List;

import java.util.List;
import lombok.Data;

@Data
public class ValidationErrorResponse {
  private String status;
  private String message;
  private List<FieldError> errors;

  public ValidationErrorResponse(String status, String message, List<FieldError> errors) {
    this.status = status;
    this.message = message;
    this.errors = errors;
  }


  @Data
  public static class FieldError {
    private String field;
    private String rejectedValue;
    private String message;

    public FieldError(String field, String rejectedValue, String message) {
      this.field = field;
      this.rejectedValue = rejectedValue;
      this.message = message;
    }

    // Getters and setters
  }
}
