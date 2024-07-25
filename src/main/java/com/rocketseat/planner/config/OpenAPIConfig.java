package com.rocketseat.planner.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI customApi() {
    return new OpenAPI().info(new Info().title("Planer documentation API").version("1.0")
        .description("This documentation there is all the endpoints built in the planner API"));
  }

//  @Bean
//  public GroupedOpenApi tripEndpoints() {
//    return GroupedOpenApi.builder().group("Trip").pathsToMatch("/trips/**").build();
//  }

//
//  @Bean
//  public GroupedOpenApi participantEndpoints() {
//    return GroupedOpenApi.builder().group("Participant").pathsToMatch("/participants/**").build();
//  }

}
