package com.rocketseat.planner.participants;

import com.rocketseat.planner.trip.Trip;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService {

  @Autowired
  private ParticipantRepository participantRepository;

  public void registerParticipantsToEvent(List<String> participantsEmails, Trip trip) {
    List<Participant> participants = participantsEmails.stream()
        .map(email -> new Participant(email, trip)).toList();
    this.participantRepository.saveAll(participants);
  }

  public InviteResponseTrip registerParticipantToEvent(String email, Trip trip) {
    Participant participant = new Participant(email, trip);
    this.participantRepository.save(participant);
    return new InviteResponseTrip(participant.getId());
  }

  public void triggerConfirmationEmailToParticipants(UUID tripId) {
  }

  public void triggerConfirmationEmailToParticipant(String email) {

  }

  public List<ParticipantsData> getAllParticipants(UUID tripId) {
    return this.participantRepository.findByTripId(tripId).stream().map(
        (participant) -> new ParticipantsData(participant.getId(), participant.getName(),
            participant.getEmail(), participant.getIsConfirmed())).toList();
  }

}
