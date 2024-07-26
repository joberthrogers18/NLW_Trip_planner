package com.rocketseat.planner.link;

import com.rocketseat.planner.trip.Trip;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkService {

  @Autowired
  private LinkRepository linkRepository;

  public LinkResponsePayload registerLinkToTrip(LinkRequestPayload linkInfo, Trip trip) {
    Link responseLink = this.linkRepository.save(new Link(linkInfo.title(), linkInfo.url(), trip));
    return new LinkResponsePayload(responseLink.getId().toString(), responseLink.getTitle(), responseLink.getUrl());
  }

  public List<LinkResponsePayload> getAllLinksById(UUID tripId) {
    return this.linkRepository.findByTripId(tripId).stream().map(
            (link) -> new LinkResponsePayload(link.getId().toString(), link.getTitle(), link.getUrl()))
        .toList();
  }

}
