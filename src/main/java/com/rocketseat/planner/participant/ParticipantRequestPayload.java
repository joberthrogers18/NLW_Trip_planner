package com.rocketseat.planner.participant;

import jakarta.validation.constraints.NotBlank;

public record ParticipantRequestPayload(
    @NotBlank(message = "name is mandatory")
    String name,

    @NotBlank(message = "email is mandatory")
    String email
) {

}
