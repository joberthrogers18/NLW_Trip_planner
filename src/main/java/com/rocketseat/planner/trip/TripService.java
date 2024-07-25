package com.rocketseat.planner.trip;

import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.exceptions.DataNotFoundException;
import com.rocketseat.planner.exceptions.RequiredArgumentsIllegalException;
import com.rocketseat.planner.participant.InviteResponseTrip;
import com.rocketseat.planner.participant.ParticipantRequestPayload;
import com.rocketseat.planner.participant.ParticipantService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
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

  private void verifyStartEndDateTrip(String startsTripDate, String endsTripDate)
      throws RequiredArgumentsIllegalException {
    LocalDateTime startsDateTrip = LocalDateTime.parse(startsTripDate,
        DateTimeFormatter.ISO_DATE_TIME);
    LocalDateTime endsDateTrip = LocalDateTime.parse(endsTripDate, DateTimeFormatter.ISO_DATE_TIME);

    if (startsDateTrip.isAfter(endsDateTrip)) {
      throw new RequiredArgumentsIllegalException("The date start date trip is happen after the ends date");
    }
  }

  private void verifyActivityOccursRangeTrip(String occursDate, Trip trip)
      throws RequiredArgumentsIllegalException {
    LocalDateTime occursDateParse = LocalDateTime.parse(occursDate,
        DateTimeFormatter.ISO_DATE_TIME);

    if ((occursDateParse.isEqual(trip.getStartsAt()) || occursDateParse.isAfter(trip.getEndsAt()))
        && (occursDateParse.isBefore(trip.getEndsAt()) || occursDateParse.isEqual(
        trip.getEndsAt()))) {
      throw new RequiredArgumentsIllegalException(
          "The occurs date activity is out of range from trip occurrence");
    }
  }

  public Trip registerTrip(TripRequestPayload payloadTrip) throws RequiredArgumentsIllegalException {
    this.verifyStartEndDateTrip(payloadTrip.starts_at(), payloadTrip.ends_at());
    Trip newTrip = new Trip(payloadTrip);
    this.tripRepository.save(newTrip);
    participantService.registerParticipantsToEvent(payloadTrip.emails_to_invite(), newTrip);
    return newTrip;
  }

  public Trip getTripById(UUID tripId) {
    return this.tripRepository.findById(tripId)
        .orElseThrow(() -> new DataNotFoundException("The trip " + tripId + " was not found"));
  }

  public Trip updateTrip(Trip baseTrip, TripRequestPayload tripPayload)
      throws RequiredArgumentsIllegalException {
    this.verifyStartEndDateTrip(tripPayload.starts_at(), tripPayload.ends_at());
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

  public String createActivityTrip(Trip trip, ActivityRequestPayload activityPayload)
      throws RequiredArgumentsIllegalException {
    this.verifyActivityOccursRangeTrip(activityPayload.occurs_at(), trip);
    UUID activityId = this.activityService.registerActivity(activityPayload.title(),
        LocalDateTime.parse(activityPayload.occurs_at(), DateTimeFormatter.ISO_LOCAL_DATE), trip);
    return activityId.toString();
  }

  public InviteResponseTrip inviteParticipantToTrip(Trip trip,
      ParticipantRequestPayload participantRequestPayload) {
    InviteResponseTrip response = this.participantService.registerParticipantToEvent(
        participantRequestPayload.email(), trip);
    if (trip.getIsConfirmed()) {
      this.participantService.triggerConfirmationEmailToParticipant(
          participantRequestPayload.email());
    }
    return response;
  }
}
