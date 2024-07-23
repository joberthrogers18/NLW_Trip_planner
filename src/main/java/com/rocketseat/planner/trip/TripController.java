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

// TODO: handle all requests with try catch

@RestController
@RequestMapping("/trips")
public class TripController {

  @Autowired
  private ParticipantService participantService;

  @Autowired
  private TripRepository tripRepository;

  @Autowired
  private ActivityService activityService;

  @Autowired
  private LinkService linkService;

  // Endpoints Manipulation Trip

  @PostMapping
  public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
    Trip newTrip = new Trip(payload);

    this.tripRepository.save(newTrip);
    participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

    return ResponseEntity.ok().body(new TripCreateResponse(newTrip.getId()));
  }

  @GetMapping("/{tripId}")
  public ResponseEntity<Trip> getTripById(@PathVariable("tripId") UUID id) {
    Optional<Trip> trip = this.tripRepository.findById(id);

    return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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

    throw new DataNotFoundException("The trip " + tripId + " was not found");
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

    throw new DataNotFoundException("The trip " + tripId + " was not found");
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

    throw new DataNotFoundException("The trip " + tripId + " was not found");
  }


  @GetMapping("/{tripId}/activities")
  public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable("tripId") UUID tripId) {
    Optional<Trip> trip = this.tripRepository.findById(tripId);

    if (trip.isPresent()) {
      List<ActivityData> activities = this.activityService.getAllActivitiesFromId(tripId);
      return ResponseEntity.ok(activities);
    }

    throw new DataNotFoundException("The trip " + tripId + " was not found");
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

    throw new DataNotFoundException("The trip " + tripId + " was not found");
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

    throw new DataNotFoundException("The trip " + tripId + " was not found");
  }

  // TODO: improve to deal error globally and when recover data that not exist show the specifically response

  @GetMapping("/{tripId}/links")
  public ResponseEntity<List<LinkResponsePayload>> getAALinks(@PathVariable("tripId") UUID tripId) {
    Optional<Trip> trip = this.tripRepository.findById(tripId);

    if (trip.isPresent()) {
      List<LinkResponsePayload> linksTrip = this.linkService.getAllLinksById(tripId);
      return ResponseEntity.ok(linksTrip);
    }

    throw new DataNotFoundException("The trip " + tripId + " was not found");
  }

}
