package edu.fer.drumre.backend.video.rating;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {

  @Query("SELECT AVG(rating.rating) FROM UserRating rating WHERE rating.video.id = :videoId")
  double averageRatingByVideoId(@Param("videoId") long videoId);

  Optional<UserRating> findByUserIdAndVideoUuid(long userId, UUID videoUuid);

  boolean existsByUserId(long userId);
}
