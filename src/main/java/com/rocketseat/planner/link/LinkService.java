package com.rocketseat.planner.link;

import com.rocketseat.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;

public class LinkService {

  @Autowired
  private LinkRepository linkRepository;

  public LinkResponsePayload registerLinkToTrip (LinkRequestPayload linkInfo, Trip trip) {
    Link link = new Link(linkInfo.title(), linkInfo.url(), trip);
    this.linkRepository.save(link);
    return new LinkResponsePayload(link.getId().toString(), link.getTitle(), link.getUrl());
  }

}
