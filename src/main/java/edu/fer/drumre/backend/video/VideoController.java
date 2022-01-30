package edu.fer.drumre.backend.video;

import edu.fer.drumre.backend.user.User;
import edu.fer.drumre.backend.user.UserService;
import edu.fer.drumre.backend.video.rating.UserRatingMapper;
import edu.fer.drumre.backend.video.rating.UserRatingService;
import edu.fer.drumre.backend.video.rating.dto.RatingResponse;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.BiFunction;
import java.util.UUID;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/video")
public class VideoController {

  private final VideoService videoService;
  private final UserRatingMapper userRatingMapper;
  private final UserService userService;
  private final UserRatingService userRatingService;

  @Autowired
  public VideoController(VideoService videoService,
      UserRatingMapper userRatingMapper,
      UserRatingService ratingService,
      UserService userService
  ) {
    this.videoService = videoService;
    this.userRatingMapper = userRatingMapper;
    this.userRatingService = ratingService;
    this.userService = userService;
  }

  @GetMapping("rating/average/{videoId}")
  public Single<ResponseEntity<RatingResponse<Double>>> getAverageRating(
      @PathVariable("videoId") UUID videoUuid
  ) {
    return videoService.getVideo(videoUuid)
        .flatMap(userRatingService::getAverageRating)
        .map(rating -> new RatingResponse<>(videoUuid, rating))
        .map(ResponseEntity::ok);
  }

  @GetMapping("rating/user/{videoId}")
  public Single<ResponseEntity<RatingResponse<Integer>>> getAverageRating(
      @RequestHeader("Authorization") String authToken,
      @PathVariable("videoId") UUID videoUuid
  ) {
    return Single.zip(
            videoService.getVideo(videoUuid),
            userService.getUserByToken(authToken),
            (BiFunction<Video, User, Pair<Video, User>>) ImmutablePair::new
        )
        .flatMap(pair -> {
          var video = pair.getLeft();
          var user = pair.getRight();
          return userRatingService.getUserRating(video, user);
        })
        .map(userRatingOptional -> {
          if (userRatingOptional.isEmpty()) {
            return new RatingResponse<>(videoUuid, 0);
          }

          return userRatingMapper.localToRemote(userRatingOptional.get());
        })
        .map(ResponseEntity::ok);
  }

  @PostMapping("rating/{videoId}/{rating}")
  public Single<ResponseEntity<RatingResponse<Integer>>> rateVideo(
      @RequestHeader("Authorization") String authToken,
      @PathVariable("videoId") UUID videoUuid,
      @PathVariable("rating") int rating
  ) {
    return Single.zip(
            videoService.getVideo(videoUuid),
            userService.getUserByToken(authToken),
            (BiFunction<Video, User, Pair<Video, User>>) ImmutablePair::new
        )
        .flatMap(pair -> {
          var video = pair.getLeft();
          var user = pair.getRight();
          return userRatingService.rateVideo(video, user, rating);
        })
        .map(userRatingMapper::localToRemote)
        .map(ResponseEntity::ok);
  }
}
