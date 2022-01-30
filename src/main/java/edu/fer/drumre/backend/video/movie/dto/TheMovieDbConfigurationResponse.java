package edu.fer.drumre.backend.video.movie.dto;

import com.google.gson.annotations.SerializedName;

public class TheMovieDbConfigurationResponse {

  Images images;

  public String getBaseUrl() {
    return images.baseUrl;
  }

  public String getSecureBaseUrl() {
    return images.secureBaseUrl;
  }

  private static class Images {

    @SerializedName("base_url")
    String baseUrl;
    @SerializedName("secure_base_url")
    String secureBaseUrl;
  }
}


