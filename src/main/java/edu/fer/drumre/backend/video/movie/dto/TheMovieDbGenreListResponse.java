package edu.fer.drumre.backend.video.movie.dto;

import java.util.List;

public class TheMovieDbGenreListResponse {

  private List<GenreResponse> genres;

  public class GenreResponse {

    int id;
    String name;

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }

  public List<GenreResponse> getGenres() {
    return genres;
  }
}
