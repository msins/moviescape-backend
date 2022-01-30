package edu.fer.drumre.backend.video.show.dto;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;
import java.util.List;

public class TheMovieDbShowDetailsResponse {

  @SerializedName("name")
  private String title;

  @SerializedName("first_air_date")
  private LocalDate firstAirDate;

  @SerializedName("number_of_episodes")
  private int numberOfEpisodes;

  @SerializedName("number_of_seasons")
  private int numberOfSeasons;

  @SerializedName("poster_path")
  private String posterPath;

  @SerializedName("overview")
  private String description;

  private List<GenreResponse> genres;

  static class GenreResponse {

    int id;
    String name;

    public String getName() {
      return name;
    }
  }

  public String getTitle() {
    return title;
  }

  public LocalDate getReleaseDate() {
    return firstAirDate;
  }

  public int getNumberOfEpisodes() {
    return numberOfEpisodes;
  }

  public int getNumberOfSeasons() {
    return numberOfSeasons;
  }

  public String getPosterPath() {
    return posterPath;
  }

  public String getDescription() {
    return description;
  }

  public List<String> getGenres() {
    return genres.stream().map(GenreResponse::getName).toList();
  }
}
