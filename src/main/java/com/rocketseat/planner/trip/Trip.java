package com.rocketseat.planner.trip;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Trip {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false, length = 255)
  private String destination;

  @Column(name = "starts_at", nullable = false)
  private LocalDateTime startsAt;

  @Column(name = "ends_at", nullable = false)
  private LocalDateTime endsAt;

  @Column(name = "is_confirmed", nullable = false)
  private Boolean isConfirmed;

  @Column(name = "owner_name", nullable = false, length = 255)
  private String ownerName;

  @Column(name = "owner_email", nullable = false, length = 255)
  private String ownerEmail;


  public Trip(TripRequestPayload data) {
    this.destination = data.destination();
    this.isConfirmed = false;
    this.ownerName = data.owner_name();
    this.ownerEmail = data.owner_email();
    this.endsAt = LocalDateTime.parse(data.end_at(), DateTimeFormatter.ISO_DATE_TIME);
    this.startsAt = LocalDateTime.parse(data.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
  }

}