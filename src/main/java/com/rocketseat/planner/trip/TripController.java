package com.rocketseat.planner.trip;

import com.rocketseat.planner.activity.ActivityData;
import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.ActivityResponsePayload;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.exceptions.DataNotFoundException;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.LinkResponsePayload;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.participant.InviteResponseTrip;
import com.rocketseat.planner.participant.ParticipantRequestPayload;
import com.rocketseat.planner.participant.ParticipantService;
import com.rocketseat.planner.participant.ParticipantsData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.swing.text.html.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trips")
public class TripController {

  private static final String ERROR_TRIP_NOT_FOUND_MESSAGE = "The trip {tripId} was not found";
  private static final String KEY_REPLACE_MESSAGE_ERROR = "{tripId}";
  private final ParticipantService participantService;
  private final TripRepository tripRepository;
  private final ActivityService activityService;
  private final LinkService linkService;
  private final TripService tripService;

  public TripController(ParticipantService participantService, TripRepository tripRepository,
      ActivityService activityService, LinkService linkService, TripService tripService) {
    this.participantService = participantService;
    this.tripRepository = tripRepository;
    this.activityService = activityService;
    this.linkService = linkService;
    this.tripService = tripService;
  }

  // Endpoints Manipulation Trip

  @PostMapping
  public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payloadTrip) {
    Trip newTrip = this.tripService.registerTrip(payloadTrip);
    return ResponseEntity.ok().body(new TripCreateResponse(newTrip.getId()));
  }

  @GetMapping("/{tripId}")
  public ResponseEntity<Trip> getTripById(@PathVariable("tripId") UUID tripId) {
    Trip tripResponse = this.tripService.getTripById(tripId);
    return ResponseEntity.ok(tripResponse);
  }

  @PutMapping("/{tripId}")
  public ResponseEntity<Trip> updateTrip(@PathVariable("tripId") UUID tripId,
      @RequestBody TripRequestPayload payload) {
    Optional<Trip> currentTrip = this.tripRepository.findById(tripId);

    if (currentTrip.isPresent()) {
      Trip rawTrip = currentTrip.get();
      rawTrip.setDestination(payload.destination());
      rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
      rawTrip.setStartsAt(
          LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));

      this.tripRepository.save(rawTrip);
      return ResponseEntity.ok(rawTrip);
    }

    throw new DataNotFoundException(
        ERROR_TRIP_NOT_FOUND_MESSAGE.replace(KEY_REPLACE_MESSAGE_ERROR, tripId.toString()));
  }

  @GetMapping("/{tripId}/confirm")
  public ResponseEntity<Trip> confirmTrip(@PathVariable("tripId") UUID tripId) {
    Optional<Trip> currentTrip = this.tripRepository.findById(tripId);

    if (currentTrip.isPresent()) {
      Trip rawTrip = currentTrip.get();
      rawTrip.setIsConfirmed(true);
      this.tripRepository.save(rawTrip);
      this.participantService.triggerConfirmationEmailToParticipants(tripId);
      return ResponseEntity.ok(rawTrip);
    }

    throw new DataNotFoundException(
        ERROR_TRIP_NOT_FOUND_MESSAGE.replace(KEY_REPLACE_MESSAGE_ERROR, tripId.toString()));
  }

  // Endpoints Activities Trip

  @PostMapping("/{tripId}/activities")
  public ResponseEntity<ActivityResponsePayload> registerActivity(
      @PathVariable("tripId") UUID tripId, @RequestBody
  ActivityRequestPayload payload) {
    Optional<Trip> trip = this.tripRepository.findById(tripId);

    if (trip.isPresent()) {
      UUID activityId = this.activityService.registerActivity(payload.title(),
          LocalDateTime.parse(payload.occurs_at(), DateTimeFormatter.ISO_LOCAL_DATE), trip.get());

      return ResponseEntity.ok(new ActivityResponsePayload(activityId.toString()));
    }

    throw new DataNotFoundException(
        ERROR_TRIP_NOT_FOUND_MESSAGE.replace(KEY_REPLACE_MESSAGE_ERROR, tripId.toString()));
  }


  @GetMapping("/{tripId}/activities")
  public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable("tripId") UUID tripId) {
    Optional<Trip> trip = this.tripRepository.findById(tripId);

    if (trip.isPresent()) {
      List<ActivityData> activities = this.activityService.getAllActivitiesFromId(tripId);
      return ResponseEntity.ok(activities);
    }

    throw new DataNotFoundException(
        ERROR_TRIP_NOT_FOUND_MESSAGE.replace(KEY_REPLACE_MESSAGE_ERROR, tripId.toString()));
  }

  // Endpoints Participants Trip

  @PostMapping("/{tripId}/invite")
  public ResponseEntity<InviteResponseTrip> inviteParticipant(@PathVariable("tripId") UUID tripId,
      @RequestBody
      ParticipantRequestPayload payload) {
    Optional<Trip> currentTrip = this.tripRepository.findById(tripId);

    if (currentTrip.isPresent()) {
      Trip rawTrip = currentTrip.get();
      InviteResponseTrip response = this.participantService.registerParticipantToEvent(
          payload.email(), rawTrip);

      if (rawTrip.getIsConfirmed()) {
        this.participantService.triggerConfirmationEmailToParticipant(payload.email());
      }

      return ResponseEntity.ok(response);
    }

    throw new DataNotFoundException(
        ERROR_TRIP_NOT_FOUND_MESSAGE.replace(KEY_REPLACE_MESSAGE_ERROR, tripId.toString()));
  }

  @GetMapping("/{tripId}/participants")
  public ResponseEntity<List<ParticipantsData>> getParticipantsFromTrip(
      @PathVariable("tripId") UUID tripId) {
    List<ParticipantsData> participants = this.participantService.getAllParticipants(tripId);
    return ResponseEntity.ok(participants);
  }

  // Endpoints Links Trip

  @PostMapping("/{tripId}/links")
  public ResponseEntity<LinkResponsePayload> registerLink(@PathVariable("tripId") UUID tripId,
      @RequestBody
      LinkRequestPayload payload) {

    Optional<Trip> trip = this.tripRepository.findById(tripId);

    if (trip.isPresent()) {
      LinkResponsePayload response = this.linkService.registerLinkToTrip(payload, trip.get());
      return ResponseEntity.ok(response);
    }

    throw new DataNotFoundException(
        ERROR_TRIP_NOT_FOUND_MESSAGE.replace(KEY_REPLACE_MESSAGE_ERROR, tripId.toString()));
  }

  @GetMapping("/{tripId}/links")
  public ResponseEntity<List<LinkResponsePayload>> getAALinks(@PathVariable("tripId") UUID tripId) {
    Optional<Trip> trip = this.tripRepository.findById(tripId);

    if (trip.isPresent()) {
      List<LinkResponsePayload> linksTrip = this.linkService.getAllLinksById(tripId);
      return ResponseEntity.ok(linksTrip);
    }

    throw new DataNotFoundException(
        ERROR_TRIP_NOT_FOUND_MESSAGE.replace(KEY_REPLACE_MESSAGE_ERROR, tripId.toString()));
  }

}
