package com.rocketseat.planner.activity;

import com.rocketseat.planner.trip.Trip;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {

  private final ActivityRepository activityRepository;

  @Autowired
  public ActivityService(ActivityRepository activityRepository) {
    this.activityRepository = activityRepository;
  }

  public UUID registerActivity(String title, LocalDateTime occursAt, Trip trip) {
    Activity activity = new Activity(title, occursAt, trip);
    this.activityRepository.save(activity);
    return activity.getId();
  }

  public List<ActivityData> getAllActivitiesFromId(UUID tripId) {
    return this.activityRepository.findByTripId(tripId).stream().map(
        activity -> new ActivityData(activity.getId(), activity.getTitle(),
            activity.getOccursAt().toString())).toList();
  }

}
