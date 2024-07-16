package com.rocketseat.planner.trip;

import com.rocketseat.planner.participants.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trips")
public class TripController {

  @Autowired
  private ParticipantService participantService;

  public ResponseEntity<String> createTrip(@RequestBody TripRequestPayload payload) {
    Trip newTrip = new Trip(payload);

    participantService.registerParticipantToEvent(payload.emails_to_invite());

    return ResponseEntity.status(HttpStatus.OK).body("");
  }

}
