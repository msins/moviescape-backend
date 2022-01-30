package edu.fer.drumre.backend.video.movie.dto;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;
import java.util.List;

public class TheMovieDbMovieWithGenreIdsResponse {

  @SerializedName("overview")
  private String description;

  @SerializedName("poster_path")
  private String posterPath;

  @SerializedName("release_date")
  private LocalDate releaseDate;

  private String title;

  @SerializedName("genre_ids")
  private List<Integer> genreIds;

  public String getDescription() {
    return description;
  }

  public String getPosterPath() {
    return posterPath;
  }

  public LocalDate getReleaseDate() {
    return releaseDate;
  }

  public String getTitle() {
    return title;
  }

  public List<Integer> getGenreIds() {
    return genreIds;
  }
}