package com.rocketseat.planner.trip;

import com.rocketseat.planner.validations.ValidISODate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record TripRequestPayload(
    @NotBlank(message = "destination is mandatory")
    String destination,

    @ValidISODate
    @NotBlank(message = "start_at is mandatory")
    String starts_at,

    @ValidISODate
    @NotBlank(message = "ends_at is mandatory")
    String ends_at,

    List<String> emails_to_invite,

    @Email(message = "The attribute owner_email is not a valid email")
    @NotBlank(message = "owner_email is mandatory")
    String owner_email,

    @NotBlank(message = "owner_name attribute is mandatory")
    String owner_name
) {

}
