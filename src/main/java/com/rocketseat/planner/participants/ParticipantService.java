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

  public void registerParticipantToEvent(List<String> participantsEmails, Trip trip) {
      List<Participant> participants = participantsEmails.stream().map(email -> new Participant(email, trip)).toList();
      this.participantRepository.saveAll(participants);
  }

  public void triggerConfirmationEmailToParticipants(UUID tripId) {}

}
