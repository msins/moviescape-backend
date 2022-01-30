package edu.fer.drumre.backend.video.analytics;

import edu.fer.drumre.backend.video.Video;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoClickedRepository extends JpaRepository<VideoClicked, Long> {

  @Query("SELECT new edu.fer.drumre.backend.video.analytics.VideoPopularity(video, COUNT(video.id))"
      + " FROM Video video"
      + " LEFT JOIN FETCH VideoClicked vc ON video.id = vc.video.id"
      + " WHERE TYPE(video) = :type"
      + " AND vc.time >= :time"
      + " GROUP BY video.id")
  List<VideoPopularity> findMostPopularVideosSince(
      @Param("type") Class<? extends Video> type,
      @Param("time") LocalDateTime time
  );

  @Query("SELECT new edu.fer.drumre.backend.video.analytics.VideoPopularity(video, COUNT(video.id))"
      + " FROM Video video"
      + " LEFT JOIN FETCH VideoClicked vc ON video.id = vc.video.id"
      + " WHERE TYPE(video) = :type"
      + " AND vc.time >= :time"
      + " AND vc.country.id = :countryId"
      + " GROUP BY video.id")
  List<VideoPopularity> findMostPopularVideosInCountrySince(
      @Param("type") Class<? extends Video> type,
      @Param("time") LocalDateTime time,
      @Param("countryId") long countryId
  );

  boolean existsByCountryId(long countryId);
}
