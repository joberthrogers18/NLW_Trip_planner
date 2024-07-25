package com.rocketseat.planner.link;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record LinkRequestPayload(
    @NotBlank(message = "title is mandatory")
    String title,

    @URL(message = "This is not a valid url")
    @NotBlank(message = "url is mandatory")
    String url
) {

}
