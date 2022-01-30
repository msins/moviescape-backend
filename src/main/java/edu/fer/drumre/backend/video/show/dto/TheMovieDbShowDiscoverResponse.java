package edu.fer.drumre.backend.video.show.dto;

import java.util.List;

public class TheMovieDbShowDiscoverResponse {

  private int page;
  private List<TheMovieDbShowJustId> results;
  private int totalPages;

  public int getPage() {
    return page;
  }

  public List<TheMovieDbShowJustId> getResults() {
    return results;
  }

  public int getTotalPages() {
    return totalPages;
  }
}

