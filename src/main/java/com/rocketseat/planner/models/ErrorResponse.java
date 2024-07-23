package com.rocketseat.planner.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

  private String status;
  private String message;
  private String errorDescription;

}
