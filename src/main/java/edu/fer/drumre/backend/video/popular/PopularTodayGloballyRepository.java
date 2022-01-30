package edu.fer.drumre.backend.video.popular;

import edu.fer.drumre.backend.video.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PopularTodayGloballyRepository extends JpaRepository<PopularTodayGlobally, Long> {

  @Query("SELECT video"
      + " FROM Video video"
      + " LEFT JOIN FETCH PopularTodayGlobally ptg ON video.id = ptg.video.id"
      + " WHERE TYPE(video) = :type"
      + " ORDER BY ptg.clicks DESC")
  <T extends Video> Page<T> findPopularToday(
      @Param("type") Class<? extends Video> ofType,
      Pageable pageable
  );

}
