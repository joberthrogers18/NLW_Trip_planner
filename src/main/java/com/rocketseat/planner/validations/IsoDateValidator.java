package com.rocketseat.planner.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class IsoDateValidator implements ConstraintValidator<ValidISODate, String> {
  private static final DateTimeFormatter ISO_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  @Override
  public void initialize(ValidISODate constraintAnnotation) {
  }

  @Override
  public boolean isValid(String dateStr, ConstraintValidatorContext context) {
    if (dateStr == null) {
      return false;
    }

    try {
      ISO_DATE_FORMATTER.parse(dateStr);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

}
