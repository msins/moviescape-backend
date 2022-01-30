package edu.fer.drumre.backend.video.show.dto;

import edu.fer.drumre.backend.video.shared.GenreResponse;
import java.util.Set;
import java.util.UUID;

public record ShowDetailsResponse(
    UUID movieId,
    String title,
    int year,
    int numberOfEpisodes,
    int numberOfSeasons,
    String coverUrl,
    Set<GenreResponse> genres,
    String description
) {

}
