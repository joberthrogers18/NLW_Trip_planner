package com.rocketseat.planner.trip;

import com.rocketseat.planner.exceptions.DataNotFoundException;
import com.rocketseat.planner.participant.ParticipantService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripService {

  private final TripRepository tripRepository;
  private final ParticipantService participantService;

  @Autowired
  public TripService(TripRepository tripRepository, ParticipantService participantService) {
    this.tripRepository = tripRepository;
    this.participantService = participantService;
  }

  public Trip registerTrip(TripRequestPayload payloadTrip) {
    Trip newTrip = new Trip(payloadTrip);
    this.tripRepository.save(newTrip);
    participantService.registerParticipantsToEvent(payloadTrip.emails_to_invite(), newTrip);
    return newTrip;
  }

  public Trip getTripById(UUID tripId) {
    return this.tripRepository.findById(tripId)
        .orElseThrow(() -> new DataNotFoundException("The trip " + tripId + " was not found"));
  }

}
