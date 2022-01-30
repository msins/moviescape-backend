package edu.fer.drumre.backend.video;

import edu.fer.drumre.backend.core.exceptions.EntityWithIdNotFound;
import edu.fer.drumre.backend.user.User;
import edu.fer.drumre.backend.user.UserRepository;
import edu.fer.drumre.backend.user.UserService;
import edu.fer.drumre.backend.video.movie.Movie;
import edu.fer.drumre.backend.video.rating.UserRating;
import edu.fer.drumre.backend.video.rating.UserRatingRepository;
import edu.fer.drumre.backend.video.rating.UserRatingService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.BiFunction;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VideoService {

  private final VideoRepository videoRepository;

  @Autowired
  public VideoService(VideoRepository videoRepository) {
    this.videoRepository = videoRepository;
  }

  @Transactional(readOnly = true)
  public Single<Video> getVideo(UUID videoUuid) {
    return Single.create(subscriber -> {
      var videoOptional = videoRepository.findByUuid(videoUuid);
      if (videoOptional.isEmpty()) {
        subscriber.onError(new EntityWithIdNotFound(Video.class, videoUuid));
        return;
      }

      subscriber.onSuccess(videoOptional.get());
    });
  }
}
