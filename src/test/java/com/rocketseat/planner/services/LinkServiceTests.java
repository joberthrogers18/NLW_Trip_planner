package com.rocketseat.planner.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rocketseat.planner.link.Link;
import com.rocketseat.planner.link.LinkRepository;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.LinkResponsePayload;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.trip.Trip;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class LinkServiceTests {

  @Mock
  private LinkRepository linkRepository;

  @InjectMocks
  private LinkService linkService;

  @Test
  public void testRegisterLinkTrip() {
    Link link = new Link();
    link.setId(UUID.randomUUID());
    link.setTitle("Test link");
    link.setUrl("https://teste.com");
    when(this.linkRepository.save(any(Link.class))).thenReturn(link);

    Trip tripTest = Trip.builder().destination("Test Destination").ownerEmail("test@gmail.com")
        .ownerName("Test name").startsAt(LocalDateTime.now()).isConfirmed(false)
        .endsAt(LocalDateTime.now()).build();
    LinkRequestPayload linkInfoTest = new LinkRequestPayload(link.getTitle(), link.getUrl());
    LinkResponsePayload response = this.linkService.registerLinkToTrip(linkInfoTest, tripTest);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(response.title(), linkInfoTest.title());
    Assertions.assertEquals(response.url(), linkInfoTest.url());
  }

  @Test
  public void testGetAllLinksTrip() {
    Link link = new Link();
    link.setId(UUID.randomUUID());
    link.setUrl("http://test.com");
    link.setTitle("Test title");

    when(this.linkRepository.findByTripId(any(UUID.class))).thenReturn(List.of(link));

    List<LinkResponsePayload> responseLinks = this.linkService.getAllLinksById(UUID.randomUUID());

    Assertions.assertNotNull(responseLinks);
    Assertions.assertFalse(responseLinks.isEmpty());
  }

}
