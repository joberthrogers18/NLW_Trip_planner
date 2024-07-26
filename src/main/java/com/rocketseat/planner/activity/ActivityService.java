package com.rocketseat.planner.activity;

import com.rocketseat.planner.trip.Trip;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {

  @Autowired
  private ActivityRepository activityRepository;

  public UUID registerActivity(String title, LocalDateTime occursAt, Trip trip) {
    Activity activity = this.activityRepository.save(new Activity(title, occursAt, trip));
    return activity.getId();
  }

  public List<ActivityData> getAllActivitiesFromId(UUID tripId) {
    return this.activityRepository.findByTripId(tripId).stream().map(
        activity -> new ActivityData(activity.getId(), activity.getTitle(),
            activity.getOccursAt().toString())).toList();
  }

}
