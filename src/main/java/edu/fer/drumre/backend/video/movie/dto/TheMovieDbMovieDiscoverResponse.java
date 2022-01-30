package edu.fer.drumre.backend.video.movie.dto;

import java.util.List;

public class TheMovieDbMovieDiscoverResponse {

  private int page;
  private List<TheMovieDbMovieWithGenreIdsResponse> results;
  private int totalPages;

  public int getPage() {
    return page;
  }

  public List<TheMovieDbMovieWithGenreIdsResponse> getResults() {
    return results;
  }

  public int getTotalPages() {
    return totalPages;
  }
}

