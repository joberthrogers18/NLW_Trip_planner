package com.rocketseat.planner.activity;

import com.rocketseat.planner.trip.Trip;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivityService {

  @Autowired
  private ActivityRepository activityRepository;

  public UUID registerActivity(String title, LocalDateTime occursAt, Trip trip) {
    Activity activity = new Activity(title, occursAt, trip);
    this.activityRepository.save(activity);
    return activity.getId();
  }

}
