package com.rocketseat.planner.participants;

import java.util.UUID;

public record ParticipantsData(UUID id, String name, String email, Boolean isConfirmed) {

}
