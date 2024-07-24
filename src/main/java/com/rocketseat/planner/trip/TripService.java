package com.rocketseat.planner.trip;

import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.ActivityResponsePayload;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.exceptions.DataNotFoundException;
import com.rocketseat.planner.participant.ParticipantService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TripService {

  private final TripRepository tripRepository;
  private final ActivityService activityService;
  private final ParticipantService participantService;

  @Autowired
  public TripService(TripRepository tripRepository, ParticipantService participantService,
      ActivityService activityService) {
    this.tripRepository = tripRepository;
    this.participantService = participantService;
    this.activityService = activityService;
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

  public Trip updateTrip(Trip baseTrip, TripRequestPayload tripPayload) {
    baseTrip.setDestination(tripPayload.destination());
    baseTrip.setEndsAt(LocalDateTime.parse(tripPayload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
    baseTrip.setStartsAt(
        LocalDateTime.parse(tripPayload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));

    this.tripRepository.save(baseTrip);
    return baseTrip;
  }

  public Trip confirmTrip(Trip baseTrip) {
    baseTrip.setIsConfirmed(true);
    this.tripRepository.save(baseTrip);
    this.participantService.triggerConfirmationEmailToParticipants(baseTrip.getId());
    return baseTrip;
  }

  public String createActivityTrip(Trip trip, ActivityRequestPayload activityPayload) {
    UUID activityId = this.activityService.registerActivity(activityPayload.title(),
        LocalDateTime.parse(activityPayload.occurs_at(), DateTimeFormatter.ISO_LOCAL_DATE), trip);
    return activityId.toString();
  }
}
