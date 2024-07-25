package com.rocketseat.planner.trip;

import com.rocketseat.planner.activity.ActivityData;
import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.ActivityResponsePayload;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.exceptions.RequiredArgumentsIllegalException;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.LinkResponsePayload;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.participant.InviteResponseTrip;
import com.rocketseat.planner.participant.ParticipantRequestPayload;
import com.rocketseat.planner.participant.ParticipantService;
import com.rocketseat.planner.participant.ParticipantsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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
@Tag(name = "Trip endpoints")
public class TripController {

  private final ParticipantService participantService;
  private final ActivityService activityService;
  private final LinkService linkService;
  private final TripService tripService;

  public TripController(ParticipantService participantService, ActivityService activityService,
      LinkService linkService, TripService tripService) {
    this.participantService = participantService;
    this.activityService = activityService;
    this.linkService = linkService;
    this.tripService = tripService;
  }

  // Endpoints Manipulation Trip

  @PostMapping
  @Operation(summary = "Create a trip", description = "This endpoint create a raw trip.")
  public ResponseEntity<TripCreateResponse> createTrip(
      @Valid @RequestBody TripRequestPayload payloadTrip) throws RequiredArgumentsIllegalException {
    Trip newTrip = this.tripService.registerTrip(payloadTrip);
    return ResponseEntity.ok().body(new TripCreateResponse(newTrip.getId()));
  }

  @GetMapping("/{tripId}")
  @Operation(summary = "Retrieve a trip by id", description = "Using id trip can return or not the expected trip.")
  public ResponseEntity<Trip> getTripById(@PathVariable("tripId") UUID tripId) {
    Trip tripResponse = this.tripService.getTripById(tripId);
    return ResponseEntity.ok(tripResponse);
  }

  @PutMapping("/{tripId}")
  @Operation(summary = "Update trip", description = "Using the tripId and update information is possible update the current trip.")
  public ResponseEntity<Trip> updateTrip(@PathVariable("tripId") UUID tripId,
      @Valid @RequestBody TripRequestPayload payload) throws RequiredArgumentsIllegalException {
    Trip tripResponse = this.tripService.getTripById(tripId);
    Trip updatedTrip = this.tripService.updateTrip(tripResponse, payload);
    return ResponseEntity.ok(updatedTrip);
  }

  @GetMapping("/{tripId}/confirm")
  @Operation(summary = "Confirm Trip", description = "Through the tripId the user can confirm a trip")
  public ResponseEntity<Trip> confirmTrip(@PathVariable("tripId") UUID tripId) {
    Trip tripResponse = this.tripService.getTripById(tripId);
    Trip updatedTrip = this.tripService.confirmTrip(tripResponse);
    return ResponseEntity.ok(updatedTrip);
  }

  // Endpoints Activities Trip

  @PostMapping("/{tripId}/activities")
  @Operation(summary = "Create activity trip", description = "The user can assign an activity to current trip")
  public ResponseEntity<ActivityResponsePayload> registerActivity(
      @PathVariable("tripId") UUID tripId, @Valid @RequestBody ActivityRequestPayload payload)
      throws RequiredArgumentsIllegalException {
    Trip tripResponse = this.tripService.getTripById(tripId);
    String activityId = this.tripService.createActivityTrip(tripResponse, payload);
    return ResponseEntity.ok(new ActivityResponsePayload(activityId));
  }


  @GetMapping("/{tripId}/activities")
  @Operation(summary = "Recover activity's trip", description = "User can visualize all the activities assigned to current trip")
  public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable("tripId") UUID tripId) {
    this.tripService.getTripById(tripId);
    List<ActivityData> activities = this.activityService.getAllActivitiesFromId(tripId);
    return ResponseEntity.ok(activities);
  }

  // Endpoints Participants Trip

  @PostMapping("/{tripId}/invite")
  @Operation(summary = "Invite participant to trip", description = "User can invite a new participant to current trip")
  public ResponseEntity<InviteResponseTrip> inviteParticipant(@PathVariable("tripId") UUID tripId,
      @Valid @RequestBody ParticipantRequestPayload payload) {
    Trip tripResponse = this.tripService.getTripById(tripId);
    InviteResponseTrip response = this.tripService.inviteParticipantToTrip(tripResponse, payload);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{tripId}/participants")
  @Operation(summary = "Recover all participant's trip", description = "This endpoint return all the participants assign to trip")
  public ResponseEntity<List<ParticipantsData>> getParticipantsFromTrip(
      @PathVariable("tripId") UUID tripId) {
    this.tripService.getTripById(tripId);
    List<ParticipantsData> participants = this.participantService.getAllParticipants(tripId);
    return ResponseEntity.ok(participants);
  }

  // Endpoints Links Trip

  @PostMapping("/{tripId}/links")
  @Operation(summary = "Add links to trip", description = "User can add links assign to trip")
  public ResponseEntity<LinkResponsePayload> registerLink(@PathVariable("tripId") UUID tripId,
      @Valid @RequestBody LinkRequestPayload payload) {
    Trip tripResponse = this.tripService.getTripById(tripId);
    LinkResponsePayload response = this.linkService.registerLinkToTrip(payload, tripResponse);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{tripId}/links")
  @Operation(summary = "Recover links from trip", description = "User can visualize all the trips assigned to current trip")
  public ResponseEntity<List<LinkResponsePayload>> getAALinks(@PathVariable("tripId") UUID tripId) {
    this.tripService.getTripById(tripId);
    List<LinkResponsePayload> linksTrip = this.linkService.getAllLinksById(tripId);
    return ResponseEntity.ok(linksTrip);
  }

}
