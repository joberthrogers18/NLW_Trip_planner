package com.rocketseat.planner.trip;

import com.rocketseat.planner.participants.ParticipantService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trips")
public class TripController {

  @Autowired
  private ParticipantService participantService;

  @Autowired
  private TripRepository tripRepository;

  @PostMapping
  public ResponseEntity<String> createTrip(@RequestBody TripRequestPayload payload) {
    Trip newTrip = new Trip(payload);

    this.tripRepository.save(newTrip);
    participantService.registerParticipantToEvent(payload.emails_to_invite(), UUID.randomUUID());

    return ResponseEntity.ok().body("Sucesso");
  }

}
