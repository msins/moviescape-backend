package edu.fer.drumre.backend.video.movie.dto;

import java.time.LocalDate;
import java.util.List;

public record TheMovieDbMovieResponse(
    String title,
    String coverUrl,
    LocalDate releaseDate,
    List<String> genres,
    String description
) {

}
