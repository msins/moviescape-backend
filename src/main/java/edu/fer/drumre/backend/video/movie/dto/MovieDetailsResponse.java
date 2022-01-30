package edu.fer.drumre.backend.video.movie.dto;

import edu.fer.drumre.backend.video.shared.GenreResponse;
import java.util.Set;
import java.util.UUID;

public record MovieDetailsResponse(
    UUID movieId,
    String title,
    int year,
    String coverUrl,
    Set<GenreResponse> genres,
    String description
) {

}
