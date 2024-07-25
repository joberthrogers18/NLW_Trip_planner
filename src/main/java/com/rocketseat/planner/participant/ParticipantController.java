package com.rocketseat.planner.participant;

import com.rocketseat.planner.exceptions.DataNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/participants")
@Tag(name = "Participant endpoints")
public class ParticipantController {

  private final ParticipantRepository participantRepository;

  @Autowired
  public ParticipantController(ParticipantRepository participantRepository) {
    this.participantRepository = participantRepository;
  }

  @PostMapping("/{idParticipant}/confirm")
  @Operation(summary = "Participant confirmation", description = "Participant invited by owner trip can confirm the trip invited")
  public ResponseEntity<Participant> confirmParticipant(@PathVariable("idParticipant") UUID id,
      @Valid  @RequestBody ParticipantRequestPayload payload) {
    Optional<Participant> participant = this.participantRepository.findById(id);

    if (participant.isPresent()) {
      Participant rawParticipant = participant.get();
      rawParticipant.setName(payload.name());
      rawParticipant.setIsConfirmed(true);
      this.participantRepository.save(rawParticipant);

      return ResponseEntity.ok(rawParticipant);
    }

    throw new DataNotFoundException("The Participant " + id + " was not found in database");
  }

}
