package com.rocketseat.planner.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rocketseat.planner.activity.Activity;
import com.rocketseat.planner.activity.ActivityData;
import com.rocketseat.planner.activity.ActivityRepository;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.trip.Trip;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ActivityServiceTests {

  @Mock
  private ActivityRepository activityRepository;

  @InjectMocks
  private ActivityService activityService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testRegisterActivity() {
    Trip testTrip = new Trip();
    testTrip.setId(UUID.randomUUID());
    testTrip.setIsConfirmed(false);
    testTrip.setOwnerName("Test name");
    testTrip.setStartsAt(LocalDateTime.now());
    testTrip.setEndsAt(LocalDateTime.now());
    testTrip.setOwnerEmail("test@gmail.com");

    Activity testActivity = new Activity("Activity test title", LocalDateTime.now(), testTrip);
    testActivity.setId(UUID.randomUUID());

    when(activityRepository.save(any(Activity.class))).thenReturn(testActivity);

    UUID IdActivity = activityService.registerActivity(testActivity.getTitle(), LocalDateTime.now(),
        testTrip);

    Assertions.assertEquals(IdActivity, testActivity.getId());
    verify(activityRepository, times(1)).save(any(Activity.class));
    Assertions.assertTrue(true);
  }

  @Test
  public void testGetAllActivitiesFromId() {
    Trip testTrip = Trip.builder().destination("Test").isConfirmed(true)
        .startsAt(LocalDateTime.now()).endsAt(LocalDateTime.now()).ownerName("Test name")
        .ownerEmail("Test email").id(UUID.randomUUID()).build();

    Activity testActivity1 = new Activity("Activity test 1", LocalDateTime.now(), testTrip);
    Activity testActivity2 = new Activity("Activity test 2", LocalDateTime.now(), testTrip);

    List<Activity> activitiesList = List.of(testActivity1, testActivity2);
    when(activityRepository.findByTripId(any(UUID.class))).thenReturn(activitiesList);

    List<ActivityData> activities = activityService.getAllActivitiesFromId(testTrip.getId());

    verify(activityRepository, times(1)).findByTripId(any(UUID.class));
    Assertions.assertEquals(activities.size(), activitiesList.size());
  }

}
