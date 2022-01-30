package edu.fer.drumre.backend.video.rating.dto;

import java.util.UUID;

public record RatingResponse<T extends Number>(
    UUID videoId,
    T rating
) {

}
