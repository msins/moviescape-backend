package edu.fer.drumre.backend.video.movie;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieLocalRepository extends JpaRepository<Movie, Long> {

  Optional<Movie> findByUuid(UUID uuid);

  boolean existsByTitleAndReleaseDate(String title, LocalDate releaseDate);
}
