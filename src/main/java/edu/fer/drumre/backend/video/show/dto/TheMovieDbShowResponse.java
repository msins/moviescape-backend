package edu.fer.drumre.backend.video.show.dto;

import java.time.LocalDate;
import java.util.List;

public record TheMovieDbShowResponse(
    String title,
    String posterPath,
    LocalDate releaseDate,
    List<String> genres,
    String description
) {

}
