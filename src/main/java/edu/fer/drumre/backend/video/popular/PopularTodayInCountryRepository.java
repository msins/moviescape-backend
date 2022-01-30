package edu.fer.drumre.backend.video.popular;

import edu.fer.drumre.backend.video.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PopularTodayInCountryRepository extends JpaRepository<PopularTodayInCountry, Long> {

  @Query("SELECT video"
      + " FROM Video video"
      + " LEFT JOIN FETCH PopularTodayInCountry ptic ON video.id = ptic.video.id"
      + " WHERE ptic.country.code = :countryCode"
      + " AND TYPE(video) = :type"
      + " ORDER BY ptic.clicks DESC")
  <T extends Video> Page<T> findPopularByCountryCode(
      @Param("type") Class<? extends Video> ofType,
      @Param("countryCode") String countryCode,
      Pageable pageable
  );
}
