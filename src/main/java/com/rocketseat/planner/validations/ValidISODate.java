package com.rocketseat.planner.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsoDateValidator.class)
public @interface ValidISODate {
  String message() default "Invalid date format. Expected format is yyyy-MM-dd'T'HH:mm:ss'Z'";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
