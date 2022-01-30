package edu.fer.drumre.backend.video.rating;

import edu.fer.drumre.backend.core.dto.LocalToRemote;
import edu.fer.drumre.backend.video.rating.dto.RatingResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class UserRatingMapper implements LocalToRemote<UserRating, RatingResponse<Integer>> {

  @Override
  public RatingResponse<Integer> localToRemote(UserRating item) {
    return new RatingResponse<>(
        item.getVideo().getUuid(),
        item.getRating()
    );
  }
}
