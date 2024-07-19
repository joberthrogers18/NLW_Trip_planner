package com.rocketseat.planner.trip;

import com.rocketseat.planner.participants.InviteResponseTrip;
import com.rocketseat.planner.participants.Participant;
import com.rocketseat.planner.participants.ParticipantRequestPayload;
import com.rocketseat.planner.participants.ParticipantService;
import com.rocketseat.planner.participants.ParticipantsData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  @Autowired
  private ParticipantService participantService;

  @Autowired
  private TripRepository tripRepository;

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

    return ResponseEntity.notFound().build();
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

    return ResponseEntity.notFound().build();
  }

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

    return ResponseEntity.notFound().build();
  }

  @GetMapping("/{tripId}/participants")
  public ResponseEntity<List<ParticipantsData>> getParticipantsFromTrip(
      @PathVariable("tripId") UUID tripId) {
    List<ParticipantsData> participants = this.participantService.getAllParticipants(tripId);
    return ResponseEntity.ok(participants);
  }

}
