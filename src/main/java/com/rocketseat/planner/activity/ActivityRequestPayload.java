package com.rocketseat.planner.activity;

import com.rocketseat.planner.validations.ValidISODate;
import jakarta.validation.constraints.NotBlank;

public record ActivityRequestPayload(
    @NotBlank(message = "title is mandatory")
    String title,

    @ValidISODate
    @NotBlank(message = "occurs_at is mandatory")
    String occurs_at
) {
}
