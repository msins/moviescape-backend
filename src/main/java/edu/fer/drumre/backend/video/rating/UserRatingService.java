package edu.fer.drumre.backend.video.rating;

import edu.fer.drumre.backend.user.User;
import edu.fer.drumre.backend.video.Video;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.BiFunction;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRatingService {

  private final UserRatingRepository userRatingRepository;

  @Autowired
  public UserRatingService(UserRatingRepository userRatingRepository) {
    this.userRatingRepository = userRatingRepository;
  }

  @Transactional(readOnly = true)
  public Single<Double> getAverageRating(Video video) {
    return Single.create(
        subscriber -> subscriber.onSuccess(
            userRatingRepository.averageRatingByVideoId(video.getId())
        )
    );
  }

  @Transactional(readOnly = true)
  public Single<Optional<UserRating>> getUserRating(Video video, User user) {
    return Single.just(userRatingRepository.findByUserIdAndVideoUuid(
        user.getId(),
        video.getUuid()
    ));
  }

  @Transactional
  public Single<UserRating> rateVideo(Video video, User user, int rating) {
    return Single.create(subscriber -> {
      var userRatingOptional = userRatingRepository.findByUserIdAndVideoUuid(
          user.getId(),
          video.getUuid()
      );

      UserRating userRating;

      if (userRatingOptional.isPresent()) {
        userRating = userRatingOptional.get();
        userRating.setRating(rating);
      } else {
        userRating = new UserRating();
        userRating.setVideo(video);
        userRating.setUser(user);
        userRating.setRating(rating);
      }

      subscriber.onSuccess(userRatingRepository.save(userRating));
    });
  }
}
