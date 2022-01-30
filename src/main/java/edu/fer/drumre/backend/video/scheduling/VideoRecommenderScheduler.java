package edu.fer.drumre.backend.video.scheduling;

import static java.util.stream.Collectors.groupingBy;

import edu.fer.drumre.backend.user.User;
import edu.fer.drumre.backend.user.UserRepository;
import edu.fer.drumre.backend.video.Video;
import edu.fer.drumre.backend.video.VideoRepository;
import edu.fer.drumre.backend.video.rating.UserRating;
import edu.fer.drumre.backend.video.rating.UserRatingRepository;
import edu.fer.drumre.backend.video.recommendations.RecommendationForUser;
import edu.fer.drumre.backend.video.recommendations.RecommendationForUserRepository;
import edu.fer.drumre.backend.video.recommendations.RecommendationService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VideoRecommenderScheduler {

  private static final Logger logger = LoggerFactory.getLogger(VideoRecommenderScheduler.class);

  private final UserRatingRepository userRatingRepository;
  private final UserRepository userRepository;
  private final VideoRepository videoRepository;
  private final RecommendationForUserRepository recommendationForUserRepository;

  @Autowired
  public VideoRecommenderScheduler(
      UserRatingRepository userRatingRepository,
      UserRepository userRepository,
      VideoRepository videoRepository,
      RecommendationForUserRepository recommendationForUserRepository
  ) {
    this.userRatingRepository = userRatingRepository;
    this.userRepository = userRepository;
    this.videoRepository = videoRepository;
    this.recommendationForUserRepository = recommendationForUserRepository;
  }

  @Scheduled(cron = "0 0 * * * *")
  @Transactional
  void updateRecommendations() {
    recommendationForUserRepository.deleteAllInBatch();
    var users = userRepository.findAll();
    var videos = videoRepository.findAll();

    for (var user : users) {
      logger.info("Generating recommendations for user: " + user.getFullName());
      calculateRecommendations(user, videos);
      logger.info("Finished generating recommendations for user: " + user.getFullName());
    }
  }

  void calculateRecommendations(User user, List<Video> videos) {
    var ratingsExist = userRatingRepository.existsByUserId(user.getId());
    if (!ratingsExist) {
      return;
    }

    var ratingMatrix = userRatingRepository.findAll()
        .stream()
        .collect(
            groupingBy(
                UserRating::getUser,
                Collectors.toMap(UserRating::getVideo, userRating -> (double) userRating.getRating())
            )
        );

    var cfMatrix = new CFMatrix(ratingMatrix);

    var recommendations = videos.stream()
        .map(video -> new RecommendationForUser(
                user,
                video,
                cfMatrix.predictScoreForVideo(user, video)
            )
        )
        .filter(recommendation -> !Double.isNaN(recommendation.getScore()))
        .toList();

    recommendationForUserRepository.saveAll(recommendations);
  }

  private static class CFMatrix {

    private final Map<User, Map<Video, Double>> ratingMatrix;

    public CFMatrix(Map<User, Map<Video, Double>> ratingMatrix) {
      this.ratingMatrix = ratingMatrix;
      normalise();
    }

    public double getUserRatingOrZero(User user, Video video) {
      return ratingMatrix.get(user).getOrDefault(video, 0.0);
    }

    public double predictScoreForVideo(User user, Video notRatedVideo) {
      Map<User, Double> similarities = calculateSimilaritiesToOtherUsers(user);

      // leave only similar ones ( > 0 )
      var toRemove = new ArrayList<User>();
      for (var entry : similarities.entrySet()) {
        if (entry.getValue() > 0.0) {
          toRemove.add(entry.getKey());
        }
      }

      toRemove.forEach(similarities.keySet()::remove);

      double ratingSimilarity = 0.0;

      for (var similarUser : similarities.keySet()) {
        ratingSimilarity += getUserRatingOrZero(similarUser, notRatedVideo) * similarities.get(similarUser);
      }

      double sumOfSimilarities = similarities.values()
          .stream()
          .mapToDouble(Double::doubleValue)
          .sum();

      return ratingSimilarity / sumOfSimilarities;
    }

    private Map<User, Double> calculateSimilaritiesToOtherUsers(User user) {
      return ratingMatrix.keySet()
          .stream()
          .filter(u -> !user.equals(u))
          .collect(Collectors.toMap(Function.identity(), u -> cosineSimilarity(user, u)));
    }

    private double cosineSimilarity(User user1, User user2) {
      double sum = 0.0;
      var disjunctiveUnionOfVideos = new HashSet<Video>();

      var user1RatedVideos = ratingMatrix.get(user1).keySet();
      var user2RatedVideos = ratingMatrix.get(user2).keySet();

      disjunctiveUnionOfVideos.addAll(user1RatedVideos);
      disjunctiveUnionOfVideos.addAll(user2RatedVideos);

      for (var video : disjunctiveUnionOfVideos) {
        double user1RatingForVideo = getUserRatingOrZero(user1, video);
        double user2RatingForVideo = getUserRatingOrZero(user2, video);

        sum += user1RatingForVideo * user2RatingForVideo;
      }

      double sumSquaresUser1 = 0.0;
      double sumSquaresUser2 = 0.0;
      for (var video : disjunctiveUnionOfVideos) {
        double user1RatingForVideo = getUserRatingOrZero(user1, video);
        double user2RatingForVideo = getUserRatingOrZero(user2, video);

        sumSquaresUser1 += Math.pow(user1RatingForVideo, 2);
        sumSquaresUser2 += Math.pow(user2RatingForVideo, 2);
      }

      if (sumSquaresUser1 * sumSquaresUser2 == 0) {
        return -1.0;
      }

      return sum / Math.sqrt(sumSquaresUser1 * sumSquaresUser2);
    }

    private void normalise() {
      for (var user : ratingMatrix.keySet()) {
        double ratingSum = ratingMatrix.get(user)
            .values()
            .stream()
            .reduce(0.0, Double::sum);

        int ratingCount = ratingMatrix.get(user).size();

        double mean = ratingSum / ratingCount;
        ratingMatrix.get(user).replaceAll((video, rating) -> rating - mean);
      }
    }
  }
}
