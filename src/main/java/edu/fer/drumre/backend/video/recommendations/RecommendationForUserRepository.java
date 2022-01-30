package edu.fer.drumre.backend.video.recommendations;

import edu.fer.drumre.backend.video.Video;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecommendationForUserRepository extends JpaRepository<RecommendationForUser, Long> {

  @Query("SELECT video "
      + " FROM Video video"
      + " LEFT JOIN FETCH RecommendationForUser rfu ON video.id = rfu.video.id"
      + " WHERE rfu.user.id = :userId"
      + " AND TYPE(video) = :type"
      + " ORDER BY rfu.score DESC")
  <T extends Video> Page<T> findAllByUserId(
      @Param("type") Class<? extends Video> ofType,
      @Param("userId") long userId,
      Pageable pageable
  );
}
