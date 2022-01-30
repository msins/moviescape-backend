package edu.fer.drumre.backend.video.recommendations;

import edu.fer.drumre.backend.user.User;
import edu.fer.drumre.backend.video.Video;
import io.reactivex.rxjava3.core.Single;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecommendationService {

  private final RecommendationForUserRepository recommendationForUserRepository;

  @Autowired
  public RecommendationService(
      RecommendationForUserRepository recommendationForUserRepository
  ) {
    this.recommendationForUserRepository = recommendationForUserRepository;
  }

  @Transactional(readOnly = true)
  public <T extends Video> Single<Page<T>> getRecommendedVideos(
      Class<T> ofType,
      User user,
      Pageable pageable
  ) {
    return Single.just(recommendationForUserRepository.findAllByUserId(
            ofType,
            user.getId(),
            pageable
        )
    );
  }
}
